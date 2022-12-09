package io.github.masachi.utils.http;

import io.github.masachi.constant.ServiceNames;
import io.github.masachi.utils.APIUtil;
import io.github.masachi.utils.BaseUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UrlUtils {

    /**
     * convert map data to HttpHeaders
     *
     * @param mapHeaders
     * @return
     */
    public static HttpHeaders convertMapToHeaders(Map<String, String> mapHeaders) {
        HttpHeaders requestHeaders = new HttpHeaders();
        mapHeaders.forEach(requestHeaders::add);
        return requestHeaders;
    }

    /**
     * 将 serviceName 和 suffixUrl 以及 param 组装成eureka用的Url
     *
     * @param serviceName
     * @param suffixUrl
     * @param params
     * @return
     */
    public static String wrapUrlForEureka(String serviceName, String suffixUrl, HttpMethod method, Object params) {
        final String servicePath = ServiceNames.getEurekaPath(serviceName, suffixUrl);

        if (HttpMethod.GET != method) {
            return servicePath;
        }

        if (!(params instanceof Map)) {
            return servicePath;
        }

        return mapToUrlParam(servicePath, (Map<String, Object>) params);
    }

    public static String wrapUrlForHttp(String serviceName, String suffixUrl, HttpMethod method, Object params) {
        String internalApiPath = APIUtil.internalApiPath(serviceName);
        if (internalApiPath.endsWith(serviceName)) {
            internalApiPath = internalApiPath.replace(serviceName, "");
        }

        String httpUrl = "";

        if (!suffixUrl.startsWith(serviceName)) {
            httpUrl = internalApiPath + serviceName + suffixUrl;
        } else {
            httpUrl = internalApiPath + suffixUrl;
        }

        if (HttpMethod.GET != method) {
            return httpUrl;
        }

        if (!(params instanceof Map)) {
            return httpUrl;
        }

        return mapToUrlParam(httpUrl, (Map<String, Object>) params);
    }

    public static String mapToUrlParam(String url, Map<String, Object> param) {
        if (BaseUtil.isEmpty(param)) {
            return url;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            sb.append(
                    String.format("%s=%s",
                            urlEncodeUTF8(entry.getKey()),
                            entry.getValue()
                    ));
            sb.append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        String urlSuffix = sb.toString();
        return url.contains("?") ? url + "&" + urlSuffix : url + "?" + urlSuffix;
    }

    static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static void main(String[] args) {
        Map map = new HashMap(2);
        map.put("a", "你好");
        map.put("b", "wws");

        final String s = mapToUrlParam("https://www.baidu.com", map);
        System.out.println(s);
    }
}
