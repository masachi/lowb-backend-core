package io.github.masachi.constant;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import io.github.masachi.utils.BaseUtil;

import java.util.Map;

@Beta
public class ServiceNames {

    public static final String Users = "/test";

    public static Map<String, String> CONTEXTPATH_TO_EUREKA_SERVICE = new ImmutableMap.Builder<String, String>()
            .put(Users, AppNames.Users.toHttp())
            .build();

    public static String getEurekaPath(String serviceName, String url) {
        if (BaseUtil.isNotEmpty(url) && BaseUtil.isNotEmpty(serviceName)) {
            return ServiceNames.CONTEXTPATH_TO_EUREKA_SERVICE.getOrDefault(serviceName,"http:/"+serviceName) + url;
        }

        return ServiceNames.CONTEXTPATH_TO_EUREKA_SERVICE.get(serviceName) + url;
    }

    public enum AppNames {
        Users(Constants.Users),
        ;

        private String appName;

        AppNames(String appName) {
            this.appName = appName;
        }


        public String toAppName() {
            return appName;
        }

        public String toHttp() {
            return "http://" + appName;
        }

        public static class Constants {
            public static final String Users="user-service";

        }
    }
}
