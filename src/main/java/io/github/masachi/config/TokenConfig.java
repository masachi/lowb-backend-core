package io.github.masachi.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:core.properties"})
public class TokenConfig implements InitializingBean {

    @Value("${token.secret}")
    private String secret;

    public static String TOKEN_SECRET;

    public static int EXPIRE_TIME = 3600 * 24 * 7 * 1000;

    public static int WX_TOKEN_EXPIRE_TIME = 60 * 10;

    @Override
    public void afterPropertiesSet() {
        TOKEN_SECRET = this.secret;
    }
}
