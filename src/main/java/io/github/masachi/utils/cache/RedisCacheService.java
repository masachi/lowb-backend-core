package io.github.masachi.utils.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import io.github.masachi.condition.RedisCondition;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
@Conditional(RedisCondition.class)
public class RedisCacheService<T> implements ICacheService<T> {
    private static final int RETRY_NUM = 3;

    @Autowired
    private RedisTemplate<String, T> redisTemplate;

    @Value("${redis.keys.partition.size:100}")
    private Integer redisKeysPartitionSize;

    @Override
    public T fetch(String key) {
        return fetch(key, null);
    }

    @Override
    public T fetch(String key, Class<T> clazz) {
        int i = 1;
        do {
            T result = null;
            try {
                result = redisTemplate.opsForValue().get(key);

                if (BaseUtil.isNotEmpty(result)) {
                    if (BaseUtil.isNull(clazz)) {
                        return result;
                    }

                    return JSON.parseObject(result.toString(), clazz);
                } else {
                    return null;
                }
            } catch (Exception e) {
                if (i == RETRY_NUM - 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("key:");
                    sb.append(key);
                    if (BaseUtil.isNotNull(result)) {
                        sb.append("<br/>");
                        sb.append("result:");
                        sb.append(result);
                    }

                    if (BaseUtil.isNotNull(clazz)) {
                        sb.append("<br/>");
                        sb.append("class:");
                        sb.append(clazz);
                    }

                    return null;
                }
            }

            i++;
        } while (i < RETRY_NUM);

        return null;
    }

    @Override
    public List<T> fetchMulti(List<String> keys) {
        return fetchMulti(keys, null);
    }

    @Override
    public List<T> fetchMulti(List<String> keys, Class<T> clazz) {
        int i = 1;
        do {
            List<T> resultList = new ArrayList<>();
            try {
                //对key进行分片处理，防止key数量太大造成压力
                List<List<String>> partitionKeysList = Lists.partition(keys, redisKeysPartitionSize);
                //使用序列化 反序列化进行取值 ， redisTemplate默认使用的就是StringSerializer
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                RedisSerializer<?> valueSerializer = redisTemplate.getValueSerializer();
                //对每个分片进行查询
                partitionKeysList.forEach(partitionKeys -> {
                    List<Object> pipelinedResult  = this.redisTemplate.executePipelined((RedisCallback<T>) connection -> {
                        for (String key : partitionKeys) {
                            valueSerializer.deserialize(connection.get(Objects.requireNonNull(keySerializer.serialize(key))));
                        }
                        return null;
                    });
                    if (BaseUtil.isNotEmpty(pipelinedResult)) {
                        resultList.addAll((Collection<? extends T>) pipelinedResult);
                    }
                });

                if (BaseUtil.isNotEmpty(resultList)) {
                    if (BaseUtil.isNull(clazz)) {
                        return resultList;
                    }
                    return JSON.parseArray(JSON.toJSONString(resultList), clazz);
//                    return resultList;
                } else {
                    return null;
                }
            } catch (Exception e) {
                if (i == RETRY_NUM - 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("keys:");
                    sb.append(keys.toString());
                    if (BaseUtil.isNotNull(resultList)) {
                        sb.append("<br/>");
                        sb.append("resultList:");
                        sb.append(resultList.toString());
                    }

                    if (BaseUtil.isNotNull(clazz)) {
                        sb.append("<br/>");
                        sb.append("class:");
                        sb.append(clazz.toString());
                    }

                    return null;
                }
            }

            i++;
        } while (i < RETRY_NUM);

        return null;
    }

    @Override
    public boolean save(Map<String, T> key2Value, int timeOut) {
        key2Value.forEach((String key, T o) -> save(key, o, timeOut));
        return true;
    }

    @Override
    public boolean save(String key, T value, int timeOut) {
        redisTemplate.opsForValue().set(key, value, timeOut, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean save(String key, T value, int timeOut, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeOut, timeUnit);
        return true;
    }

    @Override
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            return BaseUtil.isEmpty(result) ? false : result.booleanValue();
        } catch (Exception e) {

            StringBuilder sb = new StringBuilder();
            sb.append("key:");
            sb.append(key);
        }

        return false;
    }

    @Override
    public Long deleteByPattern(String pattern) {
        if (BaseUtil.isEmpty(pattern)) {
            log.error("Dangerous operation, not executed");
            return 0L;
        }

        // TODO  可随意删除的redis pattern

        Set<String> keys = redisTemplate.keys(pattern);
        if (BaseUtil.isEmpty(keys)) {
            return 0L;
        }

        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("pattern:");
            sb.append(pattern);
        }

        return 0L;
    }

    @Override
    public Long delete(List<String> keys) {
        Long deleteResult = redisTemplate.delete(keys);
        if (BaseUtil.isNotEmpty(this.redisTemplate.getConnectionFactory())) {
            RedisConnectionUtils.unbindConnection(this.redisTemplate.getConnectionFactory());
        }
        return deleteResult;
    }

    @Override
    public Boolean getLock(String key, T value, long time) {
        final T t = redisTemplate.opsForValue().get(key);
        if (BaseUtil.isNotEmpty(t)) {
            return false;
        }

        //解决redis连接释放问题，删除事务支持
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
        redisTemplate.opsForValue().setIfAbsent(key, value);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
        final List exec = redisTemplate.exec();
        if(BaseUtil.isNotEmpty(redisTemplate.getConnectionFactory())){
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return (Boolean) exec.get(0);
    }

    @Override
    public void removeLock(String key) {
        delete(key);
    }

    @Override
    public List<Map<String,Object>> scanMatchKeys(String matchKey) {
        //count(1000)中的1000是步进值，过小效率会低一些，尽量与数据级匹配些。
        ScanOptions options = ScanOptions.scanOptions().match(matchKey + "*").count(1000).build();
        //通过Cursor获取要删除的key
        Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(
                redisConnection -> new ConvertingCursor<>(redisConnection.scan(options),
                        redisTemplate.getKeySerializer()::deserialize));
        List<Map<String,Object>> keyResultMapList = new ArrayList<>();
        cursor.forEachRemaining(key -> {
            Map<String,Object> keyResultMap = new HashMap<>();
            Long expireTime = redisTemplate.opsForValue().getOperations().getExpire(key);
            keyResultMap.put("key",key);
            keyResultMap.put("expireTime",expireTime);
            keyResultMapList.add(keyResultMap);
        });
        return keyResultMapList;
    }

    @Override
    public Long getExpireTime(String key) {
        return redisTemplate.opsForValue().getOperations().getExpire(key);
    }

    @Override
    public boolean setExpireTime(String key, long time) {
        try {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("key:");
            sb.append(key);
        }
        return false;
    }

    @Override
    public List<T> range(String key, Long start, Long end, Class<T> clazz) {
        List<T> result = new ArrayList<>();

        int i = 1;
        do {
            try {
                result = redisTemplate.opsForList().range(key, start, end);

                if (BaseUtil.isNotEmpty(result)) {
                    if (BaseUtil.isNull(clazz)) {
                        return result;
                    }

                    return JSONArray.parseArray(result.toString(), clazz);
                } else {
                    return result;
                }
            } catch (Exception e) {
                if (i == RETRY_NUM - 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("key:");
                    sb.append(key);
                    if (BaseUtil.isNotNull(result)) {
                        sb.append("<br/>");
                        sb.append("result:");
                        sb.append(result);
                    }

                    if (BaseUtil.isNotNull(clazz)) {
                        sb.append("<br/>");
                        sb.append("class:");
                        sb.append(clazz);
                    }

                    return result;
                }
            }

            i++;
        } while (true);
    }

    @Override
    public Long rightPushAll(String key, List<T> values, int timeOut) {
        Long listSize = redisTemplate.opsForList().rightPushAll(key, values);
        redisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
        return listSize;
    }

}
