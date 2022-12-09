package io.github.masachi.annotation.aspect;

import com.google.common.util.concurrent.RateLimiter;
import io.github.masachi.annotation.limit.Limit;
import io.github.masachi.annotation.limit.LimitType;
import io.github.masachi.constant.Constants;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Log4j2
//@Component
@Deprecated
public class ColumnDefinitionHelper {

    @PostConstruct
    public void init() {
        log.info("初始化---------- ColumnDefinitionHelper");
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RequestMapping *)")
    public void requestMapping() {}

    @Around("requestMapping()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // TODO
        HttpServletRequest request = null;
        for (Object object : joinPoint.getArgs()) {
            if (object instanceof HttpServletRequest) {
                request = (HttpServletRequest) object;
            }
        }

        if(BaseUtil.isEmpty(request)) {
            return joinPoint.proceed();
        }

        String columnDefinitionHeader = request.getHeader(Constants.COLUMN_DEFINITION);
        if(BaseUtil.isEmpty(columnDefinitionHeader)) {
            return joinPoint.proceed();
        }

        Object returnEntity = joinPoint.getTarget();

        return joinPoint.proceed();
    }
}
