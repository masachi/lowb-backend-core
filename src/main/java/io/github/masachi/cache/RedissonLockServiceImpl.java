package io.github.masachi.cache;

import io.github.masachi.condition.RedisCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
@Conditional(RedisCondition.class)
@AllArgsConstructor
@Data
public class RedissonLockServiceImpl<T> implements ILockService<T> {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Boolean getLock(String key, T value, long time) {

        RLock rLock = redissonClient.getLock(key);
        try {
            if (rLock.tryLock(1, time, TimeUnit.SECONDS)) {
                try {
                    return true;
                } catch (Exception ignore) {
                    return false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void removeLock(String key) {
        try {
            final RLock rLock = redissonClient.getLock(key);
            rLock.unlock();
        }catch (Exception ignore){

        }
    }
}
