package io.github.masachi.filter.encrypt;

import io.github.masachi.constant.Constants;
import io.github.masachi.utils.BaseUtil;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(3)
public class EncryptFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String method = req.getMethod();

        //let OPTIONS in application
        if (HttpMethod.OPTIONS.equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String haveEncyReq = req.getHeader(Constants.REQ_ENCRYPT);
        // 不传表示加密 传了表示不加密
        if (BaseUtil.isEmpty(haveEncyReq) || !"true".equalsIgnoreCase(haveEncyReq)) {
            if (HttpMethod.POST.name().equalsIgnoreCase(method) || HttpMethod.PUT.name().equalsIgnoreCase(method)) {
                filterChain.doFilter(new ContentCachingRequestWrapper(req), response);
            } else {
                filterChain.doFilter(request, response);
            }
            return;
        }

        filterChain.doFilter(new EncryptRequestWrapper(req), response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
        System.out.println("destroy");
    }
}
