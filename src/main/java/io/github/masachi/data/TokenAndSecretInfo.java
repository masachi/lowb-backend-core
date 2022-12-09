package io.github.masachi.data;

import io.github.masachi.annotation.CacheKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@CacheKey("token")
public class TokenAndSecretInfo implements Serializable {

    /**
     * 普通的用户登录信息
     */
    private UserInfo userInfo;

}
