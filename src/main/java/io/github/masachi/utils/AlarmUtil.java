package io.github.masachi.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import io.github.masachi.exceptions.InvalidateArgumentException;
import io.github.masachi.exceptions.ServerErrorException;
import io.github.masachi.utils.mail.MailHelper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

@Log4j2
@Component
public class AlarmUtil {

    private static final Map<String, String> SQL_ERROR_MESSAGE_CONSTANTS = initErrorMapMessage();

    public void doAlarm(Throwable e) {
        doAlarmInternal(e, null, null, false);
    }

    public void doAlarm(Throwable e, HttpServletRequest request) {
        doAlarm(e, request, false);
    }

    /**
     * <p>error进行邮件发送</p>
     *
     * @param e 异常信息
     */
    public void doAlarm(Throwable e, HttpServletRequest request, boolean forceSend) {
        doAlarmInternal(e, request, null, forceSend);
    }

    /**
     * <p>error进行邮件发送</p>
     *
     * @param e 异常信息
     */
    public void doAlarmWithMessage(Throwable e, String message, boolean forceSend) {
        doAlarmInternal(e, null, message, forceSend);
    }

    /**
     * <p>error进行邮件发送</p>
     *
     * @param e       异常信息
     * @param project 项目名
     */
    public void doAlarmByProjectWithMessage(Throwable e, String message, boolean forceSend, String project) {
        doAlarmInternalByProject(e, null, message, forceSend, project);
    }


    public void doAlarm(Throwable e, String message) {
        doAlarmInternal(e, null, message, false);
    }

    public void doAlarm(String message) {
        ServerErrorException e = new ServerErrorException(1, message, "AlarmUtil-Message");
        doAlarmInternal(e, null, null, false);
    }

    private void doAlarmInternal(Throwable e, HttpServletRequest request, String message, boolean forceSend) {
        String applicationName = SpringBeanUtils.getEnvProperty("spring.application.name");
        if (BaseUtil.isEmpty(applicationName)) {
            throw new InvalidateArgumentException(1, "服务名称必填");
        }
        this.doAlarmInternalByProject(e, request, message, forceSend, applicationName);
    }

    private void doAlarmInternalByProject(Throwable e, HttpServletRequest request, String message, boolean forceSend, String project) {
        if (e instanceof NestedServletException) {
            e = e.getCause();
        }

        if (BaseUtil.isEmpty(message)) {
            message = "";
        }

        if (BaseUtil.isNotEmpty(e)) {
            message += wrapExceptionMessage(e, request);
        }

        if (!forceSend && !checkSendEnv(e)) {
            log.info("log is ignored!!! {} ", message);
            return;
        }

        if (BaseUtil.isEmpty(message)) {
            return;
        }

        // TODO send mail
        log.debug("alarm email is sending !!!");
    }


    private boolean checkSendEnv(Throwable error) {
        return (error instanceof OutOfMemoryError
                || error instanceof ServerErrorException
                || error instanceof DataAccessException
                || error instanceof NullPointerException
                || error instanceof RedisConnectionFailureException
                || error.getClass().getName().startsWith("org.apache.ibatis")
                || error instanceof HttpMessageNotReadableException);
    }

    private String wrapExceptionMessage(Throwable e, HttpServletRequest request) {
        final String errorClassName = e.getClass().getName();
        final String errorMessage = e.getMessage();
        StringBuilder sb = new StringBuilder();
        if (BaseUtil.isNotNull(request)) {
            String method = request.getMethod();
            sb.append("<br/>");
            sb.append("【request】:");
            sb.append("<br/>");
            sb.append(method);
            sb.append(" ");
            sb.append(request.getRequestURI());
            sb.append("<br/>");
            sb.append("<br/>");

            sb.append("【token】:");
            sb.append("<br/>");
            Map<String, String> header = APIUtil.getHeaderWithToken(request);
            sb.append(header);
            sb.append("<br/>");
            sb.append("<br/>");

            String referer=request.getHeader("referer");
            if(BaseUtil.isNotEmpty(referer)){
                sb.append("【referer】: ").append(referer);
                sb.append("<br/>");
                sb.append("<br/>");
            }

            final String ipAdrress = IpAddressUtil.getIpAdrress(request);
            sb.append("【IP】: ").append(ipAdrress);
            sb.append("<br/><br/>");
        }

        final String abstractErrorMessage = getAbstractErrorMessage(errorClassName);
        if (BaseUtil.isNotEmpty(abstractErrorMessage)) {
            sb.append("【错误摘要】:").append(abstractErrorMessage);
            sb.append("<br/><br/>");

            String errorSql = fetchSqlFromErrorMessage(errorMessage);
            sb.append("【Bad Sql】:").append(errorSql);
            sb.append("<br/><br/>");
        }
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("【sky-walking】:");
        sb.append("<br/>");
        sb.append("traceId : ").append(TraceContext.traceId());
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("【location】:");
        sb.append("<br/>");
        String ip = null;
        String hostName = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {

        }
        sb.append("ip : ").append(BaseUtil.isEmpty(ip) ? "获取失败" : ip);
        sb.append("<br/>");
        sb.append("hostName : ").append(BaseUtil.isEmpty(hostName) ? "获取失败" : hostName);
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("【message】:");
        sb.append("<br/>");
        sb.append(errorMessage);
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("【callStack】:");

        final Throwable[] suppressExceptions = e.getSuppressed();
        if (BaseUtil.isNotEmpty(suppressExceptions)) {
            for (Throwable exception : suppressExceptions) {
                if (exception instanceof InvocationTargetException) {
                    final InvocationTargetException targetException = (InvocationTargetException) exception;
                    //this is cause for sql error
                    if (BaseUtil.isNotEmpty(targetException.getTargetException())) {
                        sb.append(targetException.getTargetException().getMessage());
                    }
                }
                final StackTraceElement[] stackTrace = exception.getStackTrace();
                if (BaseUtil.isNotEmpty(stackTrace)) {
                    sb.append("<br/>");
                    sb.append(StringUtils.join(stackTrace, "<br/>"));
                }
            }
        }

        StackTraceElement[] errorInfoList = e.getStackTrace();
        if (BaseUtil.isNotEmpty(errorInfoList)) {
            sb.append("<br/>");
            sb.append(StringUtils.join(errorInfoList, "<br/>"));
        }

        if (BaseUtil.isNotNull(request)) {
            String method = request.getMethod();
            sb.append("【param】:");
            sb.append("<br/>");

            if ((HttpMethod.POST.name().equalsIgnoreCase(method) || HttpMethod.PUT.name().equalsIgnoreCase(method)) &&
                    request instanceof ContentCachingRequestWrapper
            ) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                sb.append(StringUtils.toEncodedString(wrapper.getContentAsByteArray(),
                        Charset.forName(wrapper.getCharacterEncoding())));
            } else {
                Map parameterMap = request.getParameterMap();
                if (BaseUtil.isNotEmpty(parameterMap)) {
                    sb.append(JSON.toJSONString(parameterMap));
                }
            }
            sb.append("<br/>");
            sb.append("<br/>");
        }

        return sb.toString();
    }

    private String fetchSqlFromErrorMessage(String errorMessage) {
        final String[] split = errorMessage.split("###");
        String errorSql = "";
        for (String err : split) {
            if (err.trim().startsWith("SQL")) {
                errorSql = err;
            }
        }
        return errorSql;
    }

    private String getAbstractErrorMessage(String errorClassName) {
        for (Map.Entry<String, String> entry : SQL_ERROR_MESSAGE_CONSTANTS.entrySet()) {
            final String key = entry.getKey();
            if (errorClassName.contains(key)) {
                return entry.getValue();
            }
        }
        return "";
    }

    private static Map<String, String> initErrorMapMessage() {
        return new ImmutableMap.Builder<String, String>()
                .put("CleanupFailureDataAccessException", "一项操作成功地执行，但在释放数据库资源时发生异常（例如，关闭一个Connection)")
                .put("BadSqlGrammarException", "错误的SQL")
                .put("DataAccessResourceFailureException", "数据访问资源彻底失败，例如不能连接数据库")
                .put("DataIntegrityViolationException", "Insert或Update数据时违反了完整性，例如违反了惟一性限制")
                .put("DataRetrievalFailureException", "某些数据不能被检测到，例如不能通过关键字找到一条记录")
                .put("DeadlockLoserDataAccessException", "当前的操作因为死锁而失败")
                .put("IncorrectUpdateSemanticsDataAccessException", "Update时发生某些没有预料到的情况，例如更改超过预期的记录数。当这个异常被抛出时，执行着的事务不会被回滚")
                .put("InvalidDataAccessApiUsageException", "一个数据访问的JAVA API没有正确使用，例如必须在执行前编译好的查询编译失败了")
                .put("invalidDataAccessResourceUsageException", "错误使用数据访问资源，例如用错误的SQL语法访问关系型数据库")
                .put("OptimisticLockingFailureException", "乐观锁的失败。这将由ORM工具或用户的DAO实现抛出")
                .put("TypeMismatchDataAccessException", "Java类型和数据类型不匹配，例如试图把String类型插入到数据库的数值型字段中")
                .put("UncategorizedDataAccessException", "有错误发生，但无法归类到某一更为具体的异常中")
                .put("UncategorizedSQLException", "查询语句异常，可能查询语句写错，或者是你的映射的类和或数据中与表不对应，检查你的映射配置文件")
                .build();
    }

}
