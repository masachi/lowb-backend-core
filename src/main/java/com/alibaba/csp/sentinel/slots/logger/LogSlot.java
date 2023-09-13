package com.alibaba.csp.sentinel.slots.logger;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.spi.Spi;

/**
 * A {@link com.alibaba.csp.sentinel.slotchain.ProcessorSlot} that is response for logging block exceptions
 * to provide concrete logs for troubleshooting.
 * reference：https://blog.csdn.net/xiaoyanhhh/article/details/129423369
 */
@Spi(order = Constants.ORDER_LOG_SLOT)
public class LogSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode obj, int count, boolean prioritized, Object... args)
            throws Throwable {
        try {
            fireEntry(context, resourceWrapper, obj, count, prioritized, args);
        } catch (BlockException e) {
            EagleEyeLogUtil.log(resourceWrapper.getName(), e.getClass().getSimpleName(), e.getRuleLimitApp(),
                    context.getOrigin(), e.getRule() != null ? e.getRule().getId() : null, count);
            throw e;
        } catch (Throwable e) {
            RecordLog.warn("Unexpected entry exception", e);
        }
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        try {
            fireExit(context, resourceWrapper, count, args);
        } catch (Throwable e) {
            RecordLog.warn("Unexpected entry exit exception", e);
        }
    }
}