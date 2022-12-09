package io.github.masachi.utils.http;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class HttpResultVO<T> implements Serializable {

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

    @Override
    public String toString() {
        return "HttpResultVO{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
