package io.github.masachi.filter;


import com.alibaba.fastjson.JSONObject;
import io.github.masachi.utils.BaseUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Map;
import java.util.Optional;

/**
 *流(request body)重复读
 */
@Order(1)
@WebFilter(filterName = "requestBodyFilter", urlPatterns = "/*")
public class RepeatedlyReadBodyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(((HttpServletRequest) request).getRequestURI().contains("graphql")) {
            chain.doFilter(request, response);
            return;
        }
        request = new HttpRequestWrapper((HttpServletRequest) request);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private static class HttpRequestWrapper extends HttpServletRequestWrapper {

        private String body;

        public HttpRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
                if (inputStream != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    char[] charBuffer = new char[128];
                    int bytesRead = -1;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                } else {
                    stringBuilder.append("");
                }
            } catch (IOException ex) {

            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            body = stringBuilder.toString();
            if(request.getMethod().equals("POST")) {
                if(BaseUtil.isNotEmpty(body)) {
                    Map map = JSONObject.parseObject(body, Map.class);
                    if(map.size() != 0) {
                        body = Optional.of(map.get("body")).orElse("{}").toString();
                    }
                }
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

            return new ServletInputStream(){
                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

    }
}

