package io.github.masachi.utils;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;


@Log4j2
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringBeanUtils implements ApplicationContextAware, EnvironmentAware {

    private static ApplicationContext applicationContext;

    private static Environment ENV;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        if (SpringBeanUtils.applicationContext == null) {
            SpringBeanUtils.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //根据name
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //根据类型
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeanOfType(Class<T> clazz) {
        return getApplicationContext().getBeansOfType(clazz);
    }

    public static String getEnvProperty(String name) {
        return ENV.getProperty(name);
    }

    @Override
    public void setEnvironment(Environment environment) {
        ENV = environment;
    }
}
