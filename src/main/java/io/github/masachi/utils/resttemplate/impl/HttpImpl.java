package io.github.masachi.utils.resttemplate.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.masachi.exceptions.GeneralException;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.http.HttpUtils;
import io.github.masachi.utils.http.UrlUtils;
import io.github.masachi.utils.resttemplate.RestTemplateUtils;
import io.github.masachi.utils.resttemplate.retry.RetryHandler;
import io.github.masachi.vo.RespVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.Map;

@Log4j2
@Component
@Primary
@ConditionalOnMissingBean(EurekaImpl.class)
public class HttpImpl implements RestTemplateUtils {

    private RetryHandler retryHandler = new RetryHandler();

    @Override
    public <T> T get(String serviceName, String suffixUrl, Map<String, String> headers, Map params, Class<T> clazz) {
        return get(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T get(String serviceName, String suffixUrl, Map<String, String> headers, Map params, ParameterizedTypeReference<RespVO<T>> responseType) {
        String url = UrlUtils.wrapUrlForHttp(serviceName, suffixUrl, HttpMethod.GET, params);

        String restResult = doRetry(url, headers, HttpMethod.GET, params);

        return doResult(restResult, responseType.getType());
    }

    @Override
    public <T> T post(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return post(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T post(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        String url = UrlUtils.wrapUrlForHttp(serviceName, suffixUrl, HttpMethod.POST, params);

        String restResult = doRetry(url, headers, HttpMethod.POST, params);

        return doResult(restResult, responseType.getType());
    }

    @Override
    public <T> T put(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return put(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T put(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        String url = UrlUtils.wrapUrlForHttp(serviceName, suffixUrl, HttpMethod.PUT, params);

        String restResult = doRetry(url, headers, HttpMethod.PUT, params);

        return doResult(restResult, responseType.getType());
    }

    @Override
    public <T> T delete(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return delete(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T delete(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        String url = UrlUtils.wrapUrlForHttp(serviceName, suffixUrl, HttpMethod.DELETE, params);

        String restResult = doRetry(url, headers, HttpMethod.DELETE, params);

        return doResult(restResult, responseType.getType());
    }

    @Override
    public <T> T patch(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz) {
        return patch(serviceName, suffixUrl, headers, params, ParameterizedTypeReference.forType(clazz));
    }

    @Override
    public <T> T patch(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType) {
        String url = UrlUtils.wrapUrlForHttp(serviceName, suffixUrl, HttpMethod.PATCH, params);

        String restResult = doRetry(url, headers, HttpMethod.PATCH, params);

        return doResult(restResult, responseType.getType());
    }

    @Override
    public RestTemplateUtils setRetryHandler(RetryHandler handler) {
        this.retryHandler = handler;
        return this;
    }

    private <T> T doResult(String restResult, Type type) {
        if (BaseUtil.isEmpty(restResult)) {
            return null;
        }

        boolean isRespVoClassType = type.getTypeName().contains("RespVO");

        RespVO respVO = null;
        try {
            if (isRespVoClassType) {
                respVO = JSON.parseObject(restResult, type);
            } else {
                respVO = JSON.parseObject(restResult, RespVO.class);
            }
        } catch (Exception e) {
            log.error("json parse error! {}", e.getMessage());
        }

        if (BaseUtil.isEmpty(respVO)) {
            return null;
        }

        if (respVO.haveError()) {
            throw new GeneralException(respVO.getCode(), respVO.getMessage());
        }

        if (BaseUtil.isEmpty(respVO.getData())) {
            return null;
        }

        if (isRespVoClassType) {
            return (T) respVO.getData();
        }

        return JSONObject.parseObject(JSON.toJSONString(respVO.getData()), type);
    }

    private String doRetry(String url, Map<String, String> headers, HttpMethod method, Object params) {
        String restResult = null;
        boolean needRetry;
        do {
            try {

                if (HttpMethod.GET == method) {
                    restResult = HttpUtils.doGet(url, headers);
                } else if (HttpMethod.POST == method) {
                    restResult = HttpUtils.doPost(url, headers, params);
                } else if (HttpMethod.PUT == method) {
                    restResult = HttpUtils.doPut(url, headers, params);
                } else if (HttpMethod.DELETE == method) {
                    restResult = HttpUtils.doDelete(url, headers);
                } else if (HttpMethod.PATCH == method) {
                    restResult = HttpUtils.doPatch(url, headers, params);
                }
            } catch (ConnectException e) {
                log.error("connect exception! url:{},headers:{},params:{} ,message:{}", url, headers, JSON.toJSONString(params), e.getMessage());
                log.error(e);
                needRetry = true;
                continue;
            } catch (Exception e) {
                log.error("system error! url:{},headers:{},params:{},message:{}", url, headers, JSON.toJSONString(params), e.getMessage());
                log.error(e);
                needRetry = true;
                continue;
            }

            if (BaseUtil.isEmpty(restResult)) {
                needRetry = true;
                continue;
            }

            needRetry = false;
        } while (needRetry && retryHandler.checkCanRetry());

        return restResult;
    }

}
