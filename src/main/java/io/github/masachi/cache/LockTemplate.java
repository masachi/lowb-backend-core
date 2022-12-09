package io.github.masachi.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * @description:
 **/
@Log4j2
@Component
@Data
@AllArgsConstructor
public class LockTemplate {

    @Autowired
    private ILockService<String> lockService;

    /**
     * 单次获取锁，失败即放弃
     *
     * @param key
     * @param onSuccess
     * @param onFail
     */
    public <T> T  runWithLock(String key, Supplier<T> onSuccess, Supplier<T> onFail) {
        if (!lockService.getLock(key, null, 99999999)) {
            if (ObjectUtils.isNotEmpty(onFail)) {
                try {
                   return onFail.get();
                } catch (Exception e) {
                    log.error("get lock fail, execute onFail() method fail! Exception :" + e);
                }
            }
            return null;
        }

        if (ObjectUtils.isNotEmpty(onSuccess)) {
            try {
                return onSuccess.get();
            } catch (Exception e) {
                log.error("get lock success, execute onSuccess() method fail! Exception :" + e);
            } finally {
                lockService.removeLock(key);
            }
        }
        return null;
    }


    /**
     * 获取自旋锁，如果失败会重试，直到获取成功，执行后释放。
     *
     * @param key
     * @param onSuccess
     */
    public <T> T  runWithSpinLock(String key, Supplier<T> onSuccess) {

        boolean lock = lockService.getLock(key, null, 99999999);

        if (!lock) {
            return runWithSpinLock(key, onSuccess);
        }else{
            try {
                if (ObjectUtils.isNotEmpty(onSuccess)) {
                    return onSuccess.get();
                }
            } catch (Exception e) {
                log.error("get lock success, execute onSuccess() method fail! Exception :" + e);
            } finally {
                lockService.removeLock(key);
            }
            return null;
        }
    }
}
