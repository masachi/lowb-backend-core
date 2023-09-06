package io.github.masachi.plugins.audit;

import io.github.masachi.plugins.exception.MybatisExceptionInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@ConditionalOnProperty("spring.datasource.url")
@ConditionalOnClass(name = {"org.apache.ibatis.plugin.Interceptor"})
public class AuditLogInterceptorAutoConfiguration {

    @Bean
    public AuditLogInterceptor auditLogInterceptor() {
        AuditLogInterceptor interceptor = new AuditLogInterceptor();
        Properties properties = new Properties();
        // 可以调用properties.setProperty方法来给拦截器设置一些自定义参数
        interceptor.setProperties(properties);
        return interceptor;
    }
}
