package io.github.masachi.filter.trace;

import com.alibaba.fastjson.JSONObject;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@Component
@Log4j2
@Order(4)
public class TraceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * 初始化请求链路信息：唯一key，日志初始化，body包装防止获取日志打印时后续不能继续使用
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        String method = ((HttpServletRequest) request).getMethod();

        String requestStr;


        // 获取请求头
        Enumeration<String> enumeration = ((HttpServletRequest) request).getHeaderNames();
        StringBuffer headers = new StringBuffer();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = ((HttpServletRequest) request).getHeader(name);
            headers.append(name + ":" + value).append(",");
        }
        ActiveSpan.tag("request_header:", headers.toString());
        log.debug("--->header:{}", BaseUtil.subStrByStrAndLen( headers.toString(),1024));

        if (method.equals(RequestMethod.GET.name())) {
            requestStr = JSONObject.toJSONString(request.getParameterMap());
            ActiveSpan.tag("request_params:", requestStr);
            log.debug("--->params:{}", BaseUtil.subStrByStrAndLen(requestStr,1024));

            chain.doFilter(request, response);
        } else {
            ParamsRequestWrapper requestWrapper = new ParamsRequestWrapper(
                    (HttpServletRequest) request);
            requestStr = requestWrapper.getBody();
            ActiveSpan.tag("request_params", requestStr);
            log.debug("--->params:{}", BaseUtil.subStrByStrAndLen(requestStr,1024));
            chain.doFilter(requestWrapper, response);
        }


    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }

}