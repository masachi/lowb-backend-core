package io.github.masachi.plugins.audit;

import io.github.masachi.plugins.audit.*;
import io.github.masachi.plugins.audit.dto.ChangeRowData;
import io.github.masachi.plugins.audit.dto.MybatisInvocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.util.List;

@RequiredArgsConstructor
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        )
})
@Log4j2
public class AuditLogInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 拦截目标
        Object target = invocation.getTarget();
        Object result = null;
        if (target instanceof Executor) {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            SqlCommandType sqlCommandType = ms.getSqlCommandType();
            DataParser dataParser = DataParserHolder.getDataParser(sqlCommandType);
            MybatisInvocation mybatisInvocation = new MybatisInvocation(args, ms, parameter, (Executor) target);
            boolean error = false;
            List<ChangeRowData> changeRows = null;
            try{
                // 1. 方法执行之前解析数据
                changeRows = dataParser.parseBefore(sqlCommandType, mybatisInvocation);
            }catch(Exception e){
                error = true;
                log.error(e.getMessage(), e);
            }
            // 2. 执行Update方法，除了查询之外的Insert，Delete，Update都是属于Update方法
            result = invocation.proceed();
            // 3. 方法执行之后处理数据,方法执行成功才需要记录差量
            if(result instanceof Number){
                Number ret = (Number)result;
                int retInt = ret.intValue();
                if(retInt <= 0 || error){
                    return result;
                }
                try{
                    // 放出并插入数据库
                    log.info("数据库变更操作： {} 记录: {}", sqlCommandType, changeRows);

                }catch(Exception e){
                    log.error(e.getMessage(), e);
                }
            }else{
                log.error("Executor.update() return:{}", result);
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }
}
