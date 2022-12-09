package io.github.masachi.exceptions;

import io.github.masachi.utils.BaseUtil;
import io.github.masachi.vo.RespVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * <p>操作成功方法</p>
     *
     * @param e 返回异常
     * @return 封装结果
     */
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public RespVO exceptionHandler(Throwable e, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        RespVO res = RespVO.error(e);

        doLog(e);

        // 参数不正确
        if(e.getClass().equals(MethodArgumentNotValidException.class)) {
            res.setCode(1);

            List<String> errorMessages = new ArrayList<>();
            ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().forEach((error) -> {
                if(BaseUtil.isNotEmpty(error.getDefaultMessage())) {
                    errorMessages.add(error.getDefaultMessage());
                }
            });

            res.setMessage(StringUtils.join(errorMessages, ","));
            return res;
        }

        if(e.getCause() != null) {
            if(!(e.getCause().getClass().equals(GeneralException.class)) && !(e.getCause().getClass().equals(InvalidateArgumentException.class))) {
                httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            if(e.getCause().getClass().equals(GeneralException.class)) {
                res.setCode(((GeneralException) e.getCause()).getCode());
                res.setMessage(((GeneralException) e.getCause()).getMessage());
            }

            if(e.getCause().getClass().equals(InvalidateArgumentException.class)) {
                res.setCode(((InvalidateArgumentException) e.getCause()).getCode());
                res.setMessage(((InvalidateArgumentException) e.getCause()).getMessage());
            }
        }


        return res;
    }

    /**
     * <p>打印错误日志</p>
     *
     * @param e 返回异常
     */
    private void doLog(Throwable e) {
        if (log.getLevel().intLevel() > Level.INFO.intLevel()) {
            log.error(e);
            StackTraceElement[] errorInfo = e.getStackTrace();
            for (StackTraceElement error : errorInfo) {
                log.error(error);
            }
        } else {
            log.error(e);
        }
    }
}
