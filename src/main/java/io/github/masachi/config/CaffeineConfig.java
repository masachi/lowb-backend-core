package io.github.masachi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.masachi.condition.CaffeineCondition;
import io.github.masachi.utils.BaseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Conditional(CaffeineCondition.class)
public class CaffeineConfig {

    @Bean
    @Conditional(CaffeineCondition.class)
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("cache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(24, TimeUnit.HOURS)
                // 初始的缓存空间大小
                .initialCapacity(100_000)
                // 缓存的最大条数
                .maximumSize(1000_000));
        return cacheManager;
    }

}
