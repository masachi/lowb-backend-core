package io.github.masachi.filter.login;

import io.github.masachi.constant.Constants;
import io.github.masachi.data.CheckResult;
import io.github.masachi.data.TokenAndSecretInfo;
import io.github.masachi.data.UserInfo;
import io.github.masachi.filter.login.interfazz.ITokenHandle;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.LoginUserUtil;
import io.github.masachi.utils.SpringBeanUtils;
import io.github.masachi.vo.RespVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Log4j2
@Component
@DependsOn("springBeanUtils")
public class CheckUserHelp {

    private static String CHECK_SECRET_URL;

    private static ITokenHandle TOKEN_HANDLE;

    private static ITokenHandle USER_INFO_HANDLE;

    private static String CHECK_TOKEN_AND_SECRET_URL;

    private static String GET_TOKEN_USER_INFO;

    /**
     * Check token and secret
     * to call this API, the request must have a valid token
     * the app id and secret is optional
     *
     * @param httpRequest
     * @param needCheckPolicy
     * @return
     */
    public CheckResult checkTokenAndSecret(
            HttpServletRequest httpRequest,
            boolean needCheckPolicy
    ) {
        httpRequest.setAttribute(Constants.HAVE_CHECK_INFO, 1);
        String token = LoginUserUtil.getToken(httpRequest);

        if (BaseUtil.isEmpty(token)) {
            return CheckResult.setError("没有登录");
        }

        TokenAndSecretInfo tokenAndSecretInfo = null;

        Map<String, String> headers = new HashMap<>(4);
        headers.put(Constants.TOKEN_NAME, token);
        try {
            tokenAndSecretInfo = TOKEN_HANDLE.validateTokenAndSecret(token);

            if (BaseUtil.isEmpty(tokenAndSecretInfo)) {
                return CheckResult.setError("没有登录");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(Constants.TOKEN_NAME + ": " + token);
            return CheckResult.setError(2, "system error:" + e.getMessage());
        }

        /*
         * force check policy
         */
        // TODO 获取用户roles policy 信息 check 是否符合
//        if (needCheckPolicy) {
//
//
//        }

        httpRequest.setAttribute(Constants.REQUEST_USER_AUTH_INFO, tokenAndSecretInfo.getUserInfo());
        return CheckResult.success();
    }

    /**
     * 获取登录用户信息
     *
     * @param httpRequest
     * @return
     */
    public UserInfo getUserAndAppInfo(HttpServletRequest httpRequest) {

        String token = LoginUserUtil.getToken(httpRequest);


        if (BaseUtil.isEmpty(token)) {
            return null;
        }

        TokenAndSecretInfo tokenAndSecretInfo;

        if (BaseUtil.isNotEmpty(USER_INFO_HANDLE)) {
            tokenAndSecretInfo = USER_INFO_HANDLE.validateTokenAndSecret(token);
        } else {
            Map<String, String> headers = new HashMap<>(4);
            headers.put(Constants.TOKEN_NAME, token);
            try {
                tokenAndSecretInfo = TOKEN_HANDLE.validateTokenAndSecret(token);

                if (BaseUtil.isEmpty(tokenAndSecretInfo)) {
                    return null;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error(Constants.TOKEN_NAME + ": " + token);
                return null;
            }
        }

        if (BaseUtil.isEmpty(tokenAndSecretInfo)) {
            return null;
        }

        UserInfo userInfo = tokenAndSecretInfo.getUserInfo();

        httpRequest.setAttribute(Constants.REQUEST_USER_INFO, userInfo);

        return userInfo;

    }

    private ITokenHandle getTokenHandleImpl() {
        Map<String, ITokenHandle> tokenHandles = SpringBeanUtils.getBeanOfType(ITokenHandle.class);
        if (tokenHandles.size() != 1) {
            return null;
        }

        for (ITokenHandle handle : tokenHandles.values()) {
            return handle;
        }

        return null;
    }

    @PostConstruct
    public void initializeHandle() {
        TOKEN_HANDLE = getTokenHandleImpl();
    }
}
