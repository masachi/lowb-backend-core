package io.github.masachi.utils.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ICacheService is the interface for the underline cache service (e.g. redis, memcached)
 *
 * @param <T>
 */
public interface ICacheService<T> {

    T fetch(String key);

    T fetch(String key, Class<T> clazz);

    List<T> fetchMulti(List<String> keys);

    /**
     * 批量获取数据
     * 使用的是executePipelined 集群模式下可能会存在问题  需要注意
     * @param keys
     * @param clazz
     * @return
     */
    List<T> fetchMulti(List<String> keys, Class<T> clazz);

    boolean save(Map<String, T> key2Value, int timeOut);

    boolean save(String key, T value, int timeOut);

    boolean save(String key, T value, int timeOut, TimeUnit timeUnit);

    boolean delete(String key);

    Long deleteByPattern(String pattern);

    Long delete(List<String> keys);

    /**
     * 需要手动调用removeLock 使用，不然在有过期时间内，永远获得不到锁
     *
     * @param key
     * @param value
     * @param time
     * @return
     */
    Boolean getLock(String key, T value, long time);

    void removeLock(String key);

    /**
     * 扫描匹配的keys
     * 返回所有扫描到的key及过期时间
     * @param key
     */
    List<Map<String,Object>> scanMatchKeys(String key);

    /**
     * 获取key到期时间
     * @param key
     */
    Long getExpireTime(String key);

    /**
     * 设置key过期时间
     * @param key
     * @param time  单位：秒
     * @return
     */
    boolean setExpireTime(String key,long time);

    /**
     * 获取list的区间[start, end], 如果获取所有的请传 [0, -1]
     * @param key key
     * @param start 开始下标
     * @param end 结束下标
     * @param clazz clazz
     * @return List<T>
     */
    List<T> range(String key, Long start, Long end, Class<T> clazz);

    /**
     * 向key后添加list
     * @param key key
     * @param values values
     * @param timeOut 超时时间 秒
     * @return 推送操作后的列表长度
     */
    Long rightPushAll(String key, List<T> values, int timeOut);

}
