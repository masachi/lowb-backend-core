package io.github.masachi.utils.resttemplate.retry;

import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@Log4j2
public class RetryHandler {

    private int executionCount = 1;

    private RetryMeta meta;

    public static RetryHandler DEFAULT_RETRY_HANDLER = new RetryHandler();

    public RetryHandler() {
        this.meta = new RetryMeta();
    }

    public RetryHandler(int interval, int attemptNum) {
        this.meta = new RetryMeta(interval, attemptNum);
    }

    public RetryHandler(RetryMeta meta) {
        this.meta = meta;
    }

    /**
     * 可重试校验，且自增重试次数
     * @return
     */
    public boolean checkCanRetry() {
        if (BaseUtil.isEmpty(meta)) {
            return false;
        }

        if (executionCount >= meta.getAttemptNum()) {
            return false;
        }

        try {
            TimeUnit.SECONDS.sleep(meta.getInterval());
        } catch (InterruptedException ignore) {
        }

        executionCount++;
        log.info("正在尝试第{}次重试", executionCount);
        return true;
    }

    /**
     * 仅做可重试校验
     * @return
     */
    public boolean checkRetryCount() {
        if (BaseUtil.isEmpty(meta)) {
            return false;
        }

        if (executionCount >= meta.getAttemptNum()) {
            return false;
        }
        return true;
    }

    public int getExecutionCount() {
        return executionCount;
    }
}
