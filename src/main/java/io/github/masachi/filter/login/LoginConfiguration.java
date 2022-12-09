package io.github.masachi.filter.login;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class LoginConfiguration implements WebMvcConfigurer {

    @Bean
    LoginFilter localInterceptor() {
        return new LoginFilter();
    }

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localInterceptor());
    }

}
