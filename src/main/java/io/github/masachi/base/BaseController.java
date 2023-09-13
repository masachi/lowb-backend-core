package io.github.masachi.base;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public abstract class BaseController {

    public String fallback(Throwable e) throws Throwable {
        if(BlockException.isBlockException(e)) {
           return "请求次数过多，请稍后再试";
        }

        throw e;
    }
}
