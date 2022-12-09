package io.github.masachi.filter.login.interfazz;

import io.github.masachi.data.TokenAndSecretInfo;
import io.github.masachi.data.UserInfo;

public interface ITokenHandle {

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     */
    UserInfo validateToken(String token);

    TokenAndSecretInfo validateTokenAndSecret(String token);

}
