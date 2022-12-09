package io.github.masachi.utils.resttemplate.impl;

import com.alibaba.fastjson.JSON;
import io.github.masachi.condition.RestTemplateCondition;
import io.github.masachi.exceptions.GeneralException;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.http.UrlUtils;
import io.github.masachi.utils.resttemplate.RestTemplateUtils;
import io.github.masachi.utils.resttemplate.retry.RetryHandler;
import io.github.masachi.vo.RespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;

@Log4j2
@Primary
@Component
@DependsOn("springBeanUtils")
@Conditional(RestTemplateCondition.class)
public class EurekaImpl implements RestTemplateUtils {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public <T> T get(String serviceName, String suffixUrl, Map<String, String> headers, Map params, Class<T> clazz) {
        return get(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T get(String serviceName, String suffixUrl, Map<String, String> headers, Map params, ParameterizedTypeReference<RespVO<T>> responseType) {
        return exchange(serviceName, suffixUrl, HttpMethod.GET, headers, params, responseType);
    }

    @Override
    public <T> T post(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return post(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T post(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        return exchange(serviceName, suffixUrl, HttpMethod.POST, headers, params, responseType);
    }

    @Override
    public <T> T put(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return put(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T put(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        return exchange(serviceName, suffixUrl, HttpMethod.PUT, headers, params, responseType);
    }

    @Override
    public <T> T delete(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return delete(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T delete(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        return exchange(serviceName, suffixUrl, HttpMethod.DELETE, headers, params, responseType);
    }

    @Override
    public <T> T patch(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return patch(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T patch(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        return exchange(serviceName, suffixUrl, HttpMethod.PATCH, headers, params, responseType);
    }

    @Override
    public RestTemplateUtils setRetryHandler(RetryHandler handler) {
        if (BaseUtil.isEmpty(handler)) {
            handler = RetryHandler.DEFAULT_RETRY_HANDLER;
        }
        try{
            RequestContextHolder.currentRequestAttributes().setAttribute("retryHandler", handler, 0);
        }catch (IllegalStateException ignore){

        }

        return this;
    }

    private <T> T exchange(String serviceName, String suffixUrl, HttpMethod method, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {

        String url = UrlUtils.wrapUrlForEureka(serviceName, suffixUrl, method, params);

        HttpHeaders requestHeaders = UrlUtils.convertMapToHeaders(headers);

        HttpEntity<Object> entity = new HttpEntity<>(params, requestHeaders);

        final Type paramResponseType = responseType.getType();
        Boolean isRespVoClassType = paramResponseType.getTypeName().contains("RespVO");
        if (!isRespVoClassType) {
            responseType = new ParameterizedTypeReference<RespVO<T>>() {
            };
        }

        ResponseEntity<RespVO<T>> result = doRetryExecute(url, headers, entity, method, params, responseType);

        if (BaseUtil.isEmpty(result)) {
            throw new GeneralException("request error! url:" + url + ", headers:" + headers + ",params:" + JSON.toJSONString(params) + ", message:response body is null");
        }

        if (result.getStatusCode().isError()) {
            log.error("network problem ! message: {}", result);
            if (BaseUtil.isEmpty(result.getBody())) {
                throw new GeneralException("request error! url:" + url + ", headers:" + headers + ",params:" + JSON.toJSONString(params) + ", message:response body is null");
            } else {
                throw new GeneralException("request error! url:" + url + ", headers:" + headers + ",params:" + JSON.toJSONString(params) + ", message:response body is null, message: " + result.getBody().getMessage());
            }
        }

        final RespVO<T> body = result.getBody();
        if (BaseUtil.isEmpty(body)) {
            // Also alarm here
            log.error("response is success, but the business result is empty");
            throw new GeneralException(
                    "request error! url:" + url + ", headers:" + headers + ",params:" + JSON.toJSONString(params) + ", message:response body is null"
            );
        }

        if (body.haveError()) {
            throw new GeneralException("request error! url:" + url + ", headers:" + headers + ",params:" + JSON.toJSONString(params) + ", message:" + body.getMessage());
        }

        if (isRespVoClassType) {
            return body.getData();
        }

        if (BaseUtil.isEmpty(body.getData())) {
            return null;
        }

        return JSON.parseObject(JSON.toJSONString(body.getData()), paramResponseType);
    }

    private <T> ResponseEntity<RespVO<T>> doRetryExecute(String url, Map<String, String> headers, HttpEntity<Object> entity, HttpMethod method, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        ResponseEntity<RespVO<T>> result = null;

        boolean needRetry;
        do {
            try {
                result = restTemplate.exchange(url, method, entity, responseType);
            } catch (IllegalStateException e) {
                //this exception may be cause by target service is down ,  @LoadBalanced have retry this request ,so dont retry
                log.error("eureka network error! IllegalStateException url:{}, headers:{},params:{}, message:{}", url, headers, JSON.toJSONString(params), e);
                needRetry = false;
                continue;
            } catch (RestClientException e) {
                log.error("eureka network error! RestClientException url:{}, headers:{},params:{}, message:{}", url, headers, JSON.toJSONString(params), e);
                if (e.getCause() instanceof SocketException) {
                    // alarmUtil.doAlarmWithMessage(e,  String.format("请求目标服务失联(建议重启目标服务).请查看原因: url:%s, headers:%s,params:%s, message:%s", url, headers, JSON.toJSONString(params), e.getMessage()), true);
                    needRetry = false;
                    continue;
                }

                if (e.getCause() instanceof SocketTimeoutException) {
                    // alarmUtil.doAlarmWithMessage(e,  String.format("请求超时，当前设置超时时间：20s.请查看原因: url:%s, headers:%s,params:%s, message:%s", url, headers, JSON.toJSONString(params), e.getMessage()), true);
                    needRetry = false;
                    continue;
                }

                if (BaseUtil.isNotEmpty(e.getMessage()) && e.getMessage().contains("JSON parse error")) {
                    needRetry = false;
                    result = new ResponseEntity<RespVO<T>>(RespVO.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
                    continue;
                }

                log.error("request error! url:{}, headers:{},params:{}, message:{}", url, headers, JSON.toJSONString(params), e.getMessage());
                needRetry = true;
                continue;
            }

            if (BaseUtil.isEmpty(result) || result.getStatusCode().isError()) {
                needRetry = true;
                continue;
            }

            needRetry = false;
        } while (needRetry && getRetryHandler().checkCanRetry());

        return result;
    }


}
