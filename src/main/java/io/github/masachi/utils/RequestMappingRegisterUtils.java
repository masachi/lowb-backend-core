package io.github.masachi.utils;

import io.github.masachi.base.BaseController;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class RequestMappingRegisterUtils {

    private static RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public void setHandlerMapping(RequestMappingHandlerMapping handlerMapping) {
        RequestMappingRegisterUtils.handlerMapping = handlerMapping;
    }

    // TODO 更新全量的 request mapping 到数据库 然后再做注册反注册

    public static void registerMapping(
            Class<BaseController> clazz,
            String url,
            String method
    ) {
        if (!getAllRegisteredMapping().contains(url)) {
            try {
                // TODO 这个的handler 是 bean 的名字 String
                handlerMapping.registerMapping(
                        RequestMappingInfo.paths(url).methods(RequestMethod.valueOf(method)).produces(MediaType.APPLICATION_JSON_VALUE).build(),
                        clazz.getName(),
                        clazz.getDeclaredMethod(method.toLowerCase(), HttpServletRequest.class, Map.class));

            } catch (NoSuchMethodException e) {
                log.error("接口注册错误：" + url + " error: " + e.getMessage());
            }
        }
    }

    public static void unregisterMapping(String url, String method) {
        handlerMapping.unregisterMapping(
                RequestMappingInfo.paths(url).methods(RequestMethod.valueOf(method)).build()
        );
    }

    public static List<String> getAllRegisteredMapping() {
        List<String> urls = new ArrayList<>();
        handlerMapping.getHandlerMethods().keySet().forEach(item -> {
            if (BaseUtil.isNotEmpty(item.getPathPatternsCondition())) {
                urls.addAll(item.getPathPatternsCondition().getPatterns().stream().map(PathPattern::getPatternString).collect(Collectors.toList()));
            }
        });

        return urls;
    }
}
