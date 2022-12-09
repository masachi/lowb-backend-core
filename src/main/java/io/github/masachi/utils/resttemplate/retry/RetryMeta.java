package io.github.masachi.utils.resttemplate.retry;

import lombok.Data;

@Data
public class RetryMeta {

    public static final RetryMeta NO_RETRY = new RetryMeta(1, 1);

    /**
     * 重试间隔
     * 单位为秒
     */
    private int interval;

    /**
     * 尝试次数
     */
    private int attemptNum;

    /**
     * 默认构造
     * 尝试3次
     * 每次间隔3秒
     */
    public RetryMeta() {
        this.interval = 3;
        this.attemptNum = 2;
    }

    public RetryMeta(int interval, int attemptNum) {
        this.interval = interval;
        this.attemptNum = attemptNum;
    }

}
