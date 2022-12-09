package io.github.masachi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsUtils;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // default 401
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/**").permitAll()
                .antMatchers("/graphiql", "/vendor/**").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .and()
                .headers()
                .xssProtection()
                .and()
                // TODO 关闭CSP 在有graphiql情况下
//                .contentSecurityPolicy("default-src 'self' 'unsafe-inline'")
//                .and()
                .contentTypeOptions();
    }
}
