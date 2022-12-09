package io.github.masachi.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAuthInfo {
    /**
     * 用户Id
     */
    public String id;
    /**
     * 用户token
     */
    public String token;
    /**
     * 用户名称
     */
    public String name;
    /**
     * 用户手机号码
     */
    public String mobile;
    /**
     * 数据是否需要加密返回
     */
    public Boolean needEncyRes;
}
