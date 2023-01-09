package io.github.masachi.utils.cache;

import io.github.masachi.condition.RedisCondition;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.SpringBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Order(5)
public class CacheTemplate<T> implements ICacheService<T> {

    @Value("${spring.redis.host:}")
    private String host;

    @Autowired(required = false)
    private RedisCacheService<T> redisCacheService;

    @Autowired(required = false)
    private CaffeineCacheService<T> caffeineCacheService;

    private ICacheService<T> cacheService;

    @PostConstruct
    public void initializeCacheService() {
        if(BaseUtil.isNotEmpty(host)) {
            cacheService = redisCacheService;
        } else {
            cacheService = caffeineCacheService;
        }
    }

    @Override
    public T fetch(String key) {
        return cacheService.fetch(key);
    }

    @Override
    public T fetch(String key, Class<T> clazz) {
        return cacheService.fetch(key, clazz);
    }

    @Override
    public List<T> fetchMulti(List<String> keys) {
        return cacheService.fetchMulti(keys);
    }

    @Override
    public List<T> fetchMulti(List<String> keys, Class<T> clazz) {
        return cacheService.fetchMulti(keys, clazz);
    }

    @Override
    public boolean save(Map<String, T> key2Value, int timeOut) {
        return cacheService.save(key2Value, timeOut);
    }

    @Override
    public boolean save(String key, T value, int timeOut) {
        return cacheService.save(key, value, timeOut);
    }

    @Override
    public boolean save(String key, T value, int timeOut, TimeUnit timeUnit) {
        return cacheService.save(key, value, timeOut, timeUnit);
    }

    @Override
    public boolean delete(String key) {
        return cacheService.delete(key);
    }

    @Override
    public Long deleteByPattern(String pattern) {
        return cacheService.deleteByPattern(pattern);
    }

    @Override
    public Long delete(List<String> keys) {
        return cacheService.delete(keys);
    }

    @Override
    public Boolean getLock(String key, T value, long time) {
        return cacheService.getLock(key, value, time);
    }

    @Override
    public void removeLock(String key) {
        cacheService.removeLock(key);
    }

    @Override
    public List<Map<String, Object>> scanMatchKeys(String key) {
        return cacheService.scanMatchKeys(key);
    }

    @Override
    public Long getExpireTime(String key) {
        return cacheService.getExpireTime(key);
    }

    @Override
    public boolean setExpireTime(String key, long time) {
        return cacheService.setExpireTime(key, time);
    }

    @Override
    public List<T> range(String key, Long start, Long end, Class<T> clazz) {
        return cacheService.range(key, start, end, clazz);
    }

    @Override
    public Long rightPushAll(String key, List<T> values, int timeOut) {
        return cacheService.rightPushAll(key, values, timeOut);
    }
}
