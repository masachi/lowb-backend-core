package io.github.masachi.annotation.compress;

import io.github.masachi.utils.BaseUtil;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Order(2)
public class CompressFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        //gzip request
        String contentEncoding = httpServletRequest.getHeader("Content-Encoding");
        if (BaseUtil.isNotEmpty(contentEncoding) && contentEncoding.equals("gzip")) {
            servletRequest = new GzipRequestWrapper(httpServletRequest);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
