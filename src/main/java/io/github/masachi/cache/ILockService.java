package io.github.masachi.cache;

/**
 * ICacheService is the interface for the underline cache service (e.g. redis, memcached)
 *
 * @param <T>
 */
public interface ILockService<T> {

    /**
     * 需要手动调用removeLock 使用，不然在有过期时间内，永远获得不到锁
     *
     * @param key
     * @param value
     * @param time
     * @return
     */
    Boolean getLock(String key, T value, long time);

    /**
     * 移除锁
     *
     * @param key
     */
    void removeLock(String key);

}
