package io.github.masachi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Component
public class PolicyUtils {

    private static String contextPath;

    @Value("${server.servlet.context-path:''}")
    public void setContextPath(String contextPath) {
        PolicyUtils.contextPath = contextPath;
    }

    public static Boolean checkPolicy(HttpServletRequest request, String operations, String resources) {
        Boolean checkVerbResult = checkVerb(request.getMethod(), operations);
        if (!checkVerbResult) {
            return false;
        }

        Boolean checkResourceResult = checkResource(request, resources);
        if (!checkResourceResult) {
            return false;
        }

        return true;
    }

    /**
     * 检查请求方法是否合法
     *
     * @param method
     * @param verb
     * @return
     */
    private static Boolean checkVerb(String method, String verb) {
        if (BaseUtil.isEmpty(verb)) {
            //如果没有配置，当成*处理
            verb = "*";
        }

        if (!"*".equalsIgnoreCase(verb) && !verb.contains(method)) {
            return false;
        }

        return true;
    }

    /**
     * 检查是否有权限请求资源
     *
     * @param resource
     * @return
     */
    private static Boolean checkResource(HttpServletRequest request, String resource) {
        if (BaseUtil.isEmpty(resource)) {
            return true;
        }

        String requestUri = request.getRequestURI().replace(contextPath, "");

        //是否所有的都包含
        if ("*".equalsIgnoreCase(resource)) {
            return true;
        }

        //不包含再检查是够通配符匹配
        List<String> res = Arrays.asList(resource.split("\\/"));
        List<String> urls = Arrays.asList(requestUri.split("\\/"));
        for (int i = 0; i < res.size(); i++) {
            //如果url都判断完了，这时候如果下一个为*，那就是匹配的，如果不是，就说明不匹配
            if ((urls.size()) == i) {
                return "*".equalsIgnoreCase(res.get(i)) || BaseUtil.isEmpty(res.get(i));
            }

            String ur = urls.get(i);
            String re = res.get(i);
            if ("*".equalsIgnoreCase(re) || BaseUtil.isEmpty(re)) {
                continue;
            }

            if (!ur.equalsIgnoreCase(re)) {
                return false;
            }
        }

        return true;
    }
}
