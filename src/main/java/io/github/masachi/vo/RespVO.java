package io.github.masachi.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.github.masachi.exceptions.GeneralException;
import io.github.masachi.utils.BaseUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.util.NestedServletException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RespVO<T> implements Serializable {

    /**
     * 状态字段 0:成功;1:失败
     */
    private Integer code;

    /**
     * 信息字段
     */
    private String message;

    /**
     * 数据字段
     */
    private T data;

    /**
     * Skywalking Trace ID
     */
    private String traceId = TraceContext.traceId();


    private boolean success = false;

    private boolean encrypted = true;

    /**
     * 是否操作成功 * @return
     */
    @JSONField(serialize = false, deserialize = false)
    public Boolean checkIsOk() {
        return 0 == this.code;
    }

    /**
     * 是否操作成功 * @return
     */
    @JSONField(serialize = false, deserialize = false)
    public Boolean haveError() {
        return !checkIsOk();
    }

    public static RespVO success(Object data) {
        RespVO respVO = new RespVO();
        respVO.code = 0;
        respVO.message = "OK";
        respVO.success = true;
        respVO.data = data;
        return respVO;
    }

    public static RespVO error(Integer code, Object data) {
        RespVO respVO = new RespVO();
        respVO.code = code;
        respVO.message = data.toString();
        return respVO;
    }

    public static RespVO error(Object data) {
        RespVO respVO = new RespVO();
        respVO.code = 1;
        respVO.message = data.toString();
        return respVO;
    }

    // 此处仅用于 SQL验证的返回值
    public static RespVO error(List<String> data, String message) {
        RespVO respVO = new RespVO();
        respVO.code = 1;
        respVO.message = message;
        respVO.data = data;
        return respVO;
    }

    // 此处仅用于 登录失败的返回 提供登录url
    public static RespVO error(Integer code, Map<String, String> data, String message) {
        RespVO respVO = new RespVO();
        respVO.code = BaseUtil.isNotEmpty(code) ? code : 1;
        respVO.message = message;
        respVO.data = data;
        return respVO;
    }

    public static RespVO error(Throwable error) {
        RespVO respVO = new RespVO();
        respVO.code = 1;
        respVO.message = error.getMessage();

        GeneralException generalException = error instanceof NestedServletException && error.getCause() instanceof GeneralException ? (GeneralException) error.getCause() : null;
        MethodArgumentNotValidException notValidException = error instanceof MethodArgumentNotValidException && error.getCause() instanceof MethodArgumentNotValidException ? (MethodArgumentNotValidException) error.getCause() : null;

        if (generalException != null) {
            respVO.code = generalException.getCode();
            respVO.message = generalException.getMessage();
        }

        if (notValidException != null) {
            respVO.code = 1;
            respVO.message = notValidException.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        return respVO;
    }
}
