package io.github.masachi.utils.cache;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Component
public class CaffeineCacheService<T> implements ICacheService<T> {

    private Cache<String, T> caffeineCache;

    private Integer caffeineKeysPartitionSize = 100;

    // default 24 hours
    private static int EXPIRE_TIME = 3600 * 24 * 1000;

    @PostConstruct
    public void initializeCaffeineCache() {
        // initialize caffeine
        caffeineCache = Caffeine.newBuilder()
                .expireAfterWrite(EXPIRE_TIME, TimeUnit.MILLISECONDS)
                .maximumSize(10_000)
                .build();
    }

    @Override
    public T fetch(String key) {
        return fetch(key, null);
    }

    @Override
    public T fetch(String key, Class<T> clazz) {
        try {
            T result = caffeineCache.getIfPresent(key);

            if(BaseUtil.isNotEmpty(result)) {
                if(BaseUtil.isEmpty(clazz)) {
                    return result;
                }
                return JSON.parseObject(result.toString(), clazz);
            } else {
                 return null;
            }
        }
        catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public List<T> fetchMulti(List<String> keys) {
        return fetchMulti(keys, null);
    }

    @Override
    public List<T> fetchMulti(List<String> keys, Class<T> clazz) {
        List<T> resultList = new ArrayList<>();
        try {
            //对key进行分片处理，防止key数量太大造成压力
            List<List<String>> partitionKeysList = Lists.partition(keys, caffeineKeysPartitionSize);
            //对每个分片进行查询
            partitionKeysList.forEach(partitionKeys -> {
                List<Object> partitionResult = partitionKeys.stream().map((key) -> caffeineCache.getIfPresent(key)).filter(BaseUtil::isNotEmpty).collect(Collectors.toList());
                if (BaseUtil.isNotEmpty(partitionResult)) {
                    resultList.addAll((Collection<? extends T>) partitionResult);
                }
            });

            if (BaseUtil.isNotEmpty(resultList)) {
                if (BaseUtil.isNull(clazz)) {
                    return resultList;
                }
                return JSON.parseArray(JSON.toJSONString(resultList), clazz);
            } else {
                return null;
            }
        }
        catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public boolean save(Map<String, T> key2Value, int timeOut) {
        key2Value.forEach((String key, T value) -> {
            caffeineCache.put(key, value);
        });

        return true;
    }

    @Override
    public boolean save(String key, T value, int timeOut) {
        caffeineCache.put(key, value);
        return true;
    }

    @Override
    public boolean save(String key, T value, int timeOut, TimeUnit timeUnit) {
        // FIXME caffeine not support time expire with keys
        caffeineCache.put(key, value);
        return true;
    }

    @Override
    public boolean delete(String key) {
        caffeineCache.invalidate(key);
        return true;
    }

    @Override
    public Long deleteByPattern(String pattern) {
        if(BaseUtil.isEmpty(pattern)) {
            return 0L;
        }

        List<String> keys = caffeineCache.asMap().keySet().stream().filter((keyItem) -> keyItem.matches(pattern)).collect(Collectors.toList());
        if(BaseUtil.isEmpty(keys)) {
            return 0L;
        }

        try {
            caffeineCache.invalidateAll(keys);
            return (long) keys.size();
        }
        catch (Exception e) {
            log.error(e);
        }

        return 0L;
    }

    @Override
    public Long delete(List<String> keys) {
        try {
            caffeineCache.invalidateAll(keys);
            return (long) keys.size();
        }
        catch (Exception e) {
            log.error(e);
        }

        return 0L;
    }

    @Override
    public Boolean getLock(String key, T value, long time) {
        throw new NotImplementedException();
    }

    @Override
    public void removeLock(String key) {
        throw new NotImplementedException();
    }

    @Override
    public List<Map<String, Object>> scanMatchKeys(String key) {
        throw new NotImplementedException();
    }

    @Override
    public Long getExpireTime(String key) {
        throw new NotImplementedException();
    }

    @Override
    public boolean setExpireTime(String key, long time) {
        throw new NotImplementedException();
    }

    @Override
    public List<T> range(String key, Long start, Long end, Class<T> clazz) {
        throw new NotImplementedException();
    }

    @Override
    public Long rightPushAll(String key, List<T> values, int timeOut) {
        throw new NotImplementedException();
    }
}
