package io.github.masachi.base;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Log4j2
public abstract class BaseService {

    public String fallback(Throwable e) throws Throwable {
        if(e.getClass().equals(DegradeException.class)) {
            log.error("调用出现错误，请稍后再试， 错误： {}", e.getCause().getMessage());
            return "调用出现错误，请稍后再试";
        }

        if(e.getClass().equals(FlowException.class)) {
            return "请求次数过多，请稍后再试";
        }

        throw e;
    }
}
