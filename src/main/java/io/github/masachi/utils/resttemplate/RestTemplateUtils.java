package io.github.masachi.utils.resttemplate;

import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.resttemplate.retry.RetryHandler;
import io.github.masachi.vo.RespVO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * * <blockquote><pre>
 *     use retry<多层嵌套>:
 *  * restTemplateInterface.setRetryHandler(new RetryHandler(3,3)).get(ServiceNames.CMS_USER, "/v0.1/group/baseInfo", null, para, new ParameterizedTypeReference<RespVO<Map>>(){});
 *
 *    normal use<多层嵌套>:
 *  * restTemplateInterface.get(ServiceNames.CMS_USER, "/v0.1/group/baseInfo", null, para, new ParameterizedTypeReference<RespVO<Map>>(){});
 *
 *    use class <单层嵌套>:
 *    restTemplateInterface.get(ServiceNames.CMS_USER, "/v0.1/group/baseInfo", null, para, String.class);
 *  * </pre></blockquote>
 *
 * @version 1.0
 * @since 1.0
 */
@Component("RestTemplateUtilsV2")
public interface RestTemplateUtils {

    /**
     * @param serviceName
     * @param suffixUrl   must start with / , ex :/user/v0.1/groupInfo
     * @param headers
     * @param params
     * @param clazz       业务直接所需要的返回值的类型，不支持多层嵌套类型 。ex：String，UserInfo
     * @return clazz对应的类型
     */
    <T> T get(String serviceName, String suffixUrl, Map<String, String> headers, Map params, Class<T> clazz);

    /**
     * @param serviceName
     * @param suffixUrl    must start with / , ex :/user/v0.1/groupInfo
     * @param headers
     * @param params
     * @param responseType 业务直接所需要的返回值的类型，ex：List<String>,Map<String,CustomerData<groupInfo>>
     * @param <T>          业务直接所需要的返回值的类型
     * @return responseType对应的数据类型
     */
    <T> T get(String serviceName, String suffixUrl, Map<String, String> headers, Map params, ParameterizedTypeReference<RespVO<T>> responseType);

    /**
     * @param serviceName
     * @param suffixUrl
     * @param headers
     * @param params
     * @param clazz       业务直接所需要的返回值的类型，不支持多层嵌套类型 。ex：String，UserInfo
     * @return clazz对应的类型
     */
    <T> T post(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz);

    /**
     * @param serviceName
     * @param suffixUrl    must start with / , ex :/user/v0.1/groupInfo
     * @param headers
     * @param params
     * @param responseType 业务直接所需要的返回值的类型，ex：List<String>,Map<String,CustomerData<groupInfo>>
     * @param <T>          业务直接所需要的返回值的类型
     * @return responseType对应的数据类型
     */
    <T> T post(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType);

    /**
     * @param serviceName
     * @param suffixUrl   must start with / , ex :/user/v0.1/groupInfo
     * @param headers
     * @param params
     * @param clazz       业务直接所需要的返回值的类型，不支持多层嵌套类型 。ex：String，UserInfo
     * @return clazz对应的类型
     */
    <T> T put(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz);

    /**
     * @param serviceName
     * @param suffixUrl    must start with / , ex :/user/v0.1/groupInfo
     * @param headers
     * @param params
     * @param responseType 业务直接所需要的返回值的类型，ex：List<String>,Map<String,CustomerData<groupInfo>>
     * @param <T>          业务直接所需要的返回值的类型
     * @return responseType对应的数据类型
     */
    <T> T put(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType);

    /**
     * @param serviceName
     * @param suffixUrl   must start with / , ex :/user/v0.1/groupInfo
     * @param headers
     * @param params
     * @param clazz       业务直接所需要的返回值的类型，不支持多层嵌套类型 。ex：String，UserInfo
     * @return clazz对应的类型
     */
    <T> T delete(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz);

    /**
     * @param serviceName
     * @param suffixUrl    must start with / , ex :/user/v0.1/groupInfo
     * @param headers
     * @param params
     * @param responseType 业务直接所需要的返回值的类型，ex：List<String>,Map<String,CustomerData<groupInfo>>
     * @param <T>          业务直接所需要的返回值的类型
     * @return responseType对应的数据类型
     */
    <T> T delete(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType);

    <T> T patch(String serviceName, String suffixUrl, Map<String, String> headers, Object params, Class<T> clazz);

    <T> T patch(String serviceName, String suffixUrl, Map<String, String> headers, Object params, ParameterizedTypeReference<RespVO<T>> responseType);

    /**
     * config retry info
     *
     * @param handler config retry handler
     * @return this
     */
    RestTemplateUtils setRetryHandler(RetryHandler handler);

    default RetryHandler getRetryHandler() {
        try{
            final Object retryHandler = RequestContextHolder.currentRequestAttributes().getAttribute("retryHandler", 0);
            if (BaseUtil.isEmpty(retryHandler)) {
                return RetryHandler.DEFAULT_RETRY_HANDLER;
            }

            return (RetryHandler) retryHandler;
        }catch (IllegalStateException ignore){
            return RetryHandler.DEFAULT_RETRY_HANDLER;
        }

    }

}
