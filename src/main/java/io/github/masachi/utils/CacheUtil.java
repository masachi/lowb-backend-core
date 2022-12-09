package io.github.masachi.utils;

import io.github.masachi.annotation.CacheKey;

import java.util.Date;

public class CacheUtil {

    public static <T> String toCacheKey(Class<T> clazz, String key) {
        CacheKey cacheKeyAnnotation = clazz.getAnnotation(CacheKey.class);
        if (cacheKeyAnnotation == null) {
            return clazz.getName() + "_" + key;
        }

        return cacheKeyAnnotation.value() + "_" + key;
    }

    /**
     * 计算缓存有效期
     * 若当前时间和有效期（expireTime）差值大于默认时间（defaultTime）取 默认时间，否则取时间差值
     * @param expireTime 有效期
     * @param defaultTime 默认缓存时间
     * @return
     */
    public static int getCacheTime(Date expireTime, int defaultTime) {
        if (expireTime == null) {
            return defaultTime;
        }
        long time = expireTime.getTime();

        long nowTime = System.currentTimeMillis();
        long diff = (time - nowTime) / 1000;
        if (diff > defaultTime) {
            return defaultTime;
        } else if (diff <= 0) {
            return 1;
        } else {
            return (int) diff;
        }
    }
}
