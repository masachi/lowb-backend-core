package io.github.masachi.config;

import io.github.masachi.condition.RedisCondition;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Data
@Conditional(RedisCondition.class)
public class RedissonConfig {
    @Value("${spring.application.name}")
    private String clientName;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.password}")
    private String password;


    @Value("${spring.redis.username}")
    private String username;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.database}")
    private int database;

    @Bean
    @Primary
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setUsername(username)
                .setPassword(password)
                .setDatabase(database)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(10)
                .setRetryAttempts(10)
                .setRetryInterval(1000)
                .setKeepAlive(true)
                .setClientName(clientName + "-" + profile);

        return Redisson.create(config);
    }
}
