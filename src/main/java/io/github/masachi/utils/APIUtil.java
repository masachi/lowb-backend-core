/**
 * created by FingerZhu on 2018/5/28.
 */
package io.github.masachi.utils;

import io.github.masachi.constant.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API相关的共有方法抽到这里来
 */
@Log4j2
public class APIUtil {

    private static String APP_NAME = SpringBeanUtils.getEnvProperty("spring.application.name") == null ? SpringBeanUtils.getEnvProperty("app.id") : SpringBeanUtils.getEnvProperty("spring.application.name");

    private static String enableCDN(String url, Map<String, List<String>> rules) {
        for (Map.Entry<String, List<String>> entry : rules.entrySet()) {
            if (url.startsWith(entry.getKey())) {
                return url.replace(entry.getKey(), entry.getValue().get(0));
            }
        }

        return url;
    }

    @Deprecated
    public static String internalApiBase() {
        return internalApiBase("");
    }

    public static String internalApiBase(String serviceName) {

        // TODO return domain;
        return "https://apis.cv3sarato.ga";
    }

    public static String internalApiPath(String serviceName) {
        return internalApiBase(serviceName) + serviceName;
    }

    public static String appName() {
        return APP_NAME;
    }

    /**
     * 获取Ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }

    public static Map<String, String> getHeaderWithToken(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>(4);
        headers.put(Constants.TOKEN_NAME, "");

        if (BaseUtil.isNotEmpty(request)) {
            String token = request.getHeader(Constants.TOKEN_NAME);
            if (BaseUtil.isEmpty(token)) {
                token = CookiesUtils.getCookieByName(request, Constants.TOKEN_NAME);
            }
            headers.put(Constants.TOKEN_NAME, token);
        }

        return headers;
    }
}
