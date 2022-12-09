package io.github.masachi.annotation.limit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

// TODO 建议做成filter

@Component
@Aspect
@Log4j2
public class LimitHelper {

    private static final Cache<String, RateLimiter> requestCaches = CacheBuilder.newBuilder()
            // 设置缓存个数
            .maximumSize(100000)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();

    @Around("@annotation(io.github.masachi.annotation.limit.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Limit limit = method.getAnnotation(Limit.class);

        LimitType limitType = Optional.of(limit.type()).orElse(LimitType.GLOBAL);

        RateLimiter limiter = null;

        switch (limitType) {
            case GLOBAL:{
                String globalLimitKey = StringUtils.joinWith("_", signature.getName(), "GLOBAL");
                limiter = requestCaches.get(globalLimitKey, () -> RateLimiter.create(limit.qps()));
                break;
            }
            case CUSTOM: {
                if (BaseUtil.isEmpty(limit.keyword())) {
                    break;
                }
                limiter = requestCaches.get(StringUtils.joinWith("_", signature.getName(),limit.keyword(), "CUSTOM"), () -> RateLimiter.create(limit.qps()));
                break;
            }
            case IP: {
                break;
            }
            default:
                break;
        }

        // TODO ip limit 没做
        if(BaseUtil.isNull(limiter)) {
            return joinPoint.proceed();
        }

        if (limiter.tryAcquire()) {
            // 获得令牌（不限制访问）
            return joinPoint.proceed();
        } else {
            // 未获得令牌（限制访问）
            throw new RuntimeException("频繁请求限制,请稍后重试");
        }
    }
}
