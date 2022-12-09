package io.github.masachi.utils;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Copyleft from Mr Fu
 */

@Slf4j
public class RetryUtil {

    /**
     * 根据输入的condition重复做task,在规定的次数内达到condition则返回,
     * 如果超过retryTimes则返回null, 重试次数,整个重试时间以及retry exception都会记录log
     *
     * @param task 重试做的任务
     * @param compensation 补偿逻辑
     * @return targetBean
     */
    public static <V> Optional<V> retry(Callable<V> task, Consumer<Attempt> compensation) {
        return retry(resp -> false, task, null, null,compensation);
    }

    /**
     * 根据输入的condition重复做task,在规定的次数内达到condition则返回,
     * 如果超过retryTimes则返回null, 重试次数,整个重试时间以及retry exception都会记录log
     *
     * @param task 重试做的任务
     * @return targetBean
     */
    public static <V> Optional<V> retry(Callable<V> task) {
        return retry(resp -> false, task, null, null,null);
    }

    /**
     * 根据输入的condition重复做task,在规定的次数内达到condition则返回,
     * 如果超过retryTimes则返回null, 重试次数,整个重试时间以及retry exception都会记录log
     *
     * @param condition 重试条件,比如接口返回errorCode为处理中,或不是最终需要的结果
     * @param task      重试做的任务
     * @return targetBean
     */
    public static <V> Optional<V> retry(Predicate<V> condition, Callable<V> task) {
        return retry(condition, task, null, null,null);
    }

    /**
     * 根据输入的condition重复做task,在规定的次数内达到condition则返回,
     * 如果超过retryTimes则返回null, 重试次数,整个重试时间以及retry exception都会记录log
     *
     * @param condition  重试条件,比如接口返回errorCode为处理中,或不是最终需要的结果
     * @param task       重试做的任务
     * @param sleepTime  重试间隔时间,单位毫秒
     * @param retryTimes 重试次数
     * @param compensation 补偿逻辑
     * @return targetBean
     */
    public static <V> Optional<V> retry(Predicate<V> condition, Callable<V> task, Integer sleepTime, Integer retryTimes, Consumer<Attempt> compensation) {
        Optional<V> result = Optional.empty();
        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            Retryer<V> retry = RetryerBuilder.<V>newBuilder()
                    // 默认任务执行过程中发生异常自动重试
                    .retryIfException()
                    // 重试条件（按照业务场景）
                    .retryIfResult(condition)
                    // 等待策略
                    .withWaitStrategy(WaitStrategies.fixedWait(BaseUtil.isEmpty(sleepTime) ? 3000 : sleepTime, TimeUnit.MILLISECONDS))
                    // 重试策略
                    .withStopStrategy(StopStrategies.stopAfterAttempt(BaseUtil.isEmpty(retryTimes) ? 3 : retryTimes))
                    // 重试监听器
                    .withRetryListener(new RetryListener() {
                        @Override
                        public <V> void onRetry(Attempt<V> attempt) {
                            // 记录重试次数和异常信息
                            log.debug(MessageFormat.format("{0}th retry", attempt.getAttemptNumber()));
                            if (attempt.hasException()) {
                                log.error(MessageFormat.format("retry exception:{0}", attempt.getExceptionCause()));
                            }
                            if(BaseUtil.isNotEmpty(compensation)){
                                compensation.accept(attempt);
                            }
                        }
                    }).build();

            // 开始执行重试任务
            result = Optional.ofNullable(retry.call(task));
        } catch (Exception e) {
            log.error("retry fail:", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            stopWatch.stop();
            log.debug("retry execute time", stopWatch.getTime());
        }
        return result;
    }
}
