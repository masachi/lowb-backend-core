package io.github.masachi.utils.http;

import com.alibaba.fastjson.JSON;
import io.github.masachi.utils.APIUtil;
import io.github.masachi.utils.BaseUtil;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class HttpUtils {

    private static String charset = CharEncoding.UTF_8;
    private static Integer connectTimeout = 120000;
    private static Integer socketTimeout = 120000;
    private static String proxyHost = null;
    private static Integer proxyPort = null;
    private static String JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE;

    /**
     * Do GET request
     *
     * @param url
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static String doGet(String url) throws Exception {
        return doGet(url, null);
    }

    public static String doGet(String url, Map<String, String> headers) throws Exception {
        if (haveChinese(url)) {
            UrlEntity urlEntity = parse(url);
            if (BaseUtil.isNotEmpty(urlEntity.params)) {
                String urlParam = parseRequestParam(urlEntity.params);
                url = urlEntity.baseUrl + urlParam;
            }
        }

        HttpURLConnection httpURLConnection = getHttpURLConnection(url, headers);
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);

        String tempLine;
        BufferedReader reader = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        StringBuffer resultBuffer = new StringBuffer();

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
        ) {
            return doGet(httpURLConnection.getHeaderField(HttpHeaders.LOCATION), headers);
        }

        final String responseCode = httpURLConnection.getResponseCode() + "";
        if (!responseCode.startsWith("2")) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
        } catch (Exception ignore) {

        } finally {
            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return resultBuffer.toString();
    }

    public static HttpURLConnection getHttpURLConnection(String url, Map<String, String> headers) throws IOException {
        URL localURL = new URL(url);
        URLConnection connection = openConnection(localURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        if (!headers.isEmpty()) {
            headers.forEach((n, v) -> httpURLConnection.setRequestProperty(n, v));
        }
        return httpURLConnection;
    }

    /**
     * Do Patch request
     *
     * @param url
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static String doPatch(String url) throws Exception {
        //允许patch
        allowMethods("PATCH");

        HttpURLConnection httpURLConnection = getHttpURLConnection(url, null);

        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        httpURLConnection.setRequestMethod("PATCH");

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
        ) {
            return doPatch(httpURLConnection.getHeaderField(HttpHeaders.LOCATION));
        }

        if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    /**
     * Do Patch request
     *
     * @param url
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static String doPatch(String url, Map<String, String> headers, Object parameter) throws Exception {
        //允许patch
        allowMethods("PATCH");

        HttpURLConnection httpURLConnection = getHttpURLConnection(url, headers);

        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        String param = wrapParam(contentType, parameter);

        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        httpURLConnection.setRequestMethod("PATCH");
        httpURLConnection.setDoOutput(true);


        if (BaseUtil.isEmpty(contentType)) {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        } else {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
        }
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(param.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(param);
            outputStreamWriter.flush();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                    httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ||
                    httpURLConnection.getResponseCode() == 307
            ) {
                return doPatch(httpURLConnection.getHeaderField(HttpHeaders.LOCATION), headers, parameter);
            }
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
        }finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

        }
        return resultBuffer.toString();
    }


    public static int doGetForStatus(String url) throws Exception {

        URL localURL = new URL(url);
        HttpURLConnection httpURLConnection = getHttpURLConnection(url, null);

        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return httpURLConnection.getResponseCode();
    }


    /**
     * Do POST request
     *
     * @param url
     * @param parameterMap
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map parameterMap) throws Exception {
        return doPost(url, new HashMap(1), parameterMap);
    }

    public static String doPost(String url, Object parameterMap) throws Exception {
        return doPost(url, new HashMap(1), parameterMap);
    }

    /**
     * Do POST request
     *
     * @param url
     * @param parameterMap
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, String> headers, Map parameterMap) throws Exception {
        String parameter = "";
        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        if (parameterMap != null) {
            if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)) {
                parameter = JSON.toJSONString(parameterMap);
            } else {
                parameter = convertPostParamToFormValue(parameterMap);
            }
        }
        HttpURLConnection httpURLConnection = getHttpURLConnection(url, headers);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        if (BaseUtil.isEmpty(contentType)) {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        } else {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
        }
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(parameter.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine;

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(parameter);
            outputStreamWriter.flush();


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                    httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ||
                    httpURLConnection.getResponseCode() == 307
            ) {
                return doPost(httpURLConnection.getHeaderField(HttpHeaders.LOCATION), headers, parameterMap);
            }

            final String responseCode = httpURLConnection.getResponseCode() + "";
            if (!responseCode.startsWith("2")) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    public static String doPost(String url, Map<String, String> headers, Object parameter) throws Exception {
        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        String param = wrapParam(contentType, parameter);
        HttpURLConnection httpURLConnection = getHttpURLConnection(url, headers);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        if (BaseUtil.isEmpty(contentType)) {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        } else {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
        }
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(param.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine;

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(param);
            outputStreamWriter.flush();


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                    httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ||
                    httpURLConnection.getResponseCode() == 307
            ) {
                return doPost(httpURLConnection.getHeaderField(HttpHeaders.LOCATION), headers, parameter);
            }

            final String responseCode = httpURLConnection.getResponseCode() + "";
            if (!responseCode.startsWith("2")) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }


    public static String doPut(String url, Map parameterMap) throws Exception {
        return doPut(url, new HashMap(), parameterMap);
    }

    public static String doPut(String url, Object parameterMap) throws Exception {
        return doPut(url, new HashMap(1), parameterMap);
    }

    public static String doPut(String url, Map<String, String> headers, Map parameterMap) throws Exception {
        String parameter = "";
        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        if (parameterMap != null) {
            if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)) {
                parameter = JSON.toJSONString(parameterMap);
            } else {
                parameter = convertPostParamToFormValue(parameterMap);
            }
        }

        HttpURLConnection httpURLConnection = getHttpURLConnection(url, headers);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        if (BaseUtil.isEmpty(contentType)) {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        } else {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
        }
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(parameter.length()));


        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(parameter);
            outputStreamWriter.flush();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                    httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ||
                    httpURLConnection.getResponseCode() == 307
            ) {
                return doPut(httpURLConnection.getHeaderField(HttpHeaders.LOCATION), parameterMap);
            }

            final String responseCode = httpURLConnection.getResponseCode() + "";
            if (!responseCode.startsWith("2")) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }


            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    public static String doPut(String url, Map<String, String> headers, Object parameter) throws Exception {
        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        String param = wrapParam(contentType, parameter);
        HttpURLConnection httpURLConnection = getHttpURLConnection(url, headers);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        if (BaseUtil.isEmpty(contentType)) {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        } else {
            httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
        }
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(param.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine;

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(param);
            outputStreamWriter.flush();


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                    httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ||
                    httpURLConnection.getResponseCode() == 307
            ) {
                return doPost(httpURLConnection.getHeaderField(HttpHeaders.LOCATION), headers, parameter);
            }

            final String responseCode = httpURLConnection.getResponseCode() + "";
            if (!responseCode.startsWith("2")) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    public static String doDelete(String url) throws Exception {
        return doDelete(url, null);
    }

    public static String doDelete(String url, Map<String, String> headers) throws Exception {
        if (haveChinese(url)) {
            UrlEntity urlEntity = parse(url);
            if (BaseUtil.isNotEmpty(urlEntity.params)) {
                String urlParam = parseRequestParam(urlEntity.params);
                url = urlEntity.baseUrl + urlParam;
            }
        }

        HttpURLConnection httpURLConnection = getHttpURLConnection(url, headers);
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, charset);
        httpURLConnection.setRequestMethod("DELETE");
        String tempLine;
        BufferedReader reader = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        StringBuffer resultBuffer = new StringBuffer();

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
        ) {
            return doGet(httpURLConnection.getHeaderField(HttpHeaders.LOCATION), headers);
        }

        final String responseCode = httpURLConnection.getResponseCode() + "";
        if (!responseCode.startsWith("2")) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
        } catch (Exception e) {

        } finally {
            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return resultBuffer.toString();
    }

    private static URLConnection openConnection(URL localURL) throws IOException {
        URLConnection connection;
        if (proxyHost != null && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            connection = localURL.openConnection(proxy);
        } else {
            connection = localURL.openConnection();
        }
        renderRequest(connection);
        return connection;
    }

    /**
     * Render request according setting
     */
    private static void renderRequest(URLConnection connection) {

        if (connectTimeout != null) {
            connection.setConnectTimeout(connectTimeout);
        }

        if (socketTimeout != null) {
            connection.setReadTimeout(socketTimeout);
        }

    }

    /**
     * 将请求参数拼接成 ?aa=bb&cc=dd
     */
    public static String parseRequestParam(Map<String, Object> params) throws Exception {
        String paramsStr = "";

        if (BaseUtil.isEmpty(params)) {
            return paramsStr;
        }

        StringBuilder urlParam = new StringBuilder("?");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (BaseUtil.isEmpty(entry.getValue())) {
                continue;
            }

            if (entry.getValue() instanceof String) {
                String encode = URLEncoder.encode(entry.getValue().toString(), CharEncoding.UTF_8);
                urlParam.append(entry.getKey() + "=" + encode + "&");
            } else {
                urlParam.append(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        paramsStr = urlParam.toString();
        if (paramsStr.endsWith("&")) {
            paramsStr = paramsStr.substring(0, paramsStr.length() - 1);
        }

        return paramsStr;
    }

    private static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new IllegalStateException(e);
        }
    }

    private static String convertPostParamToFormValueForMultiValueMap(MultiValueMap multiValueMapParam) {
        StringBuffer parameterBuffer = new StringBuffer();
        Iterator iterator = multiValueMapParam.keySet().iterator();
        String key;
        String value;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            if (multiValueMapParam.get(key) != null) {
                // 一个key对应多个值的时候，多个值用逗号区分
                value = StringUtils.join(((List<String>)multiValueMapParam.get(key)).toArray(),",");
                try {
                    value = URLEncoder.encode(value, CharEncoding.UTF_8);
                } catch (UnsupportedEncodingException ignore) {
                    System.out.println("encode url 失败");
                    System.out.println(ignore);
                }
            } else {
                value = "";
            }
            parameterBuffer.append(key).append("=").append(value);
            if (iterator.hasNext()) {
                parameterBuffer.append("&");
            }
        }
        return parameterBuffer.toString();
    }

    private static String convertPostParamToFormValue(Map params) {
        return doConvertPostParamToWWWFormUrlEncoded(params, CharEncoding.UTF_8);
    }

    private static String doConvertPostParamToWWWFormUrlEncoded(Map params, String charSet) {
        if (BaseUtil.isEmpty(charSet)) {
            charSet = CharEncoding.UTF_8;
        }
        StringBuffer parameterBuffer = new StringBuffer();
        Iterator iterator = params.keySet().iterator();
        String key;
        String value;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            if (params.get(key) != null) {
                value = String.valueOf(params.get(key));
                try {
                    value = URLEncoder.encode(value, charSet);
                } catch (UnsupportedEncodingException ignore) {
                    System.out.println("encode url 失败");
                    System.out.println(ignore);
                }
            } else {
                value = "";
            }
            parameterBuffer.append(key).append("=").append(value);
            if (iterator.hasNext()) {
                parameterBuffer.append("&");
            }
        }
        return parameterBuffer.toString();
    }

    private static String wrapParam(String contentType, Object param) {
        if (BaseUtil.isEmpty(param)) {
            return "";
        }

        if (JSON_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            return JSON.toJSONString(param);
        }

        if(param instanceof MultiValueMap){
            return convertPostParamToFormValueForMultiValueMap((MultiValueMap) param);
        }

        if (param instanceof Map) {
            return convertPostParamToFormValue((Map) param);
        }

        return param.toString();
    }

    public static UrlEntity parse(String url) {
        UrlEntity entity = new UrlEntity();
        if (url == null) {
            return entity;
        }
        url = url.trim();
        if (url.equals("")) {
            return entity;
        }
        String[] urlParts = url.split("\\?");
        entity.baseUrl = urlParts[0];
        //没有参数
        if (urlParts.length == 1) {
            return entity;
        }

        url = url.replaceFirst(entity.baseUrl + "\\?", "");

        //有参数
        String[] params = url.split("&");
        entity.params = new HashMap<>();
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length >= 2) {
                entity.params.put(keyValue[0], keyValue[1]);
            }
        }

        return entity;
    }

    public static boolean haveChinese(String str) {
        if (BaseUtil.isEmpty(str)) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FA5) {
                return true;
            }
        }
        return false;
    }

}
