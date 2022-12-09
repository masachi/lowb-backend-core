package io.github.masachi.utils;


import io.github.masachi.constant.Constants;
import io.github.masachi.data.UserAuthInfo;
import io.github.masachi.data.UserInfo;
import io.github.masachi.filter.login.CheckUserHelp;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class LoginUserUtil {


    /**
     * 获取用户
     *
     * @param request
     * @return
     */
    public static UserInfo getUserInfo(HttpServletRequest request) {

        Object user = request.getAttribute(Constants.REQUEST_USER_INFO);

        if (BaseUtil.isEmpty(user)) {
            return SpringBeanUtils.getBean(CheckUserHelp.class).getUserAndAppInfo(request);
        }

        return (UserInfo) user;

    }

    /**
     * 获取用户
     *
     * @param request
     * @return
     */
    public static UserAuthInfo getUserAuthInfo(HttpServletRequest request) {

        Object user = request.getAttribute(Constants.REQUEST_USER_AUTH_INFO);

        if (BaseUtil.isEmpty(user)) {
            return null;
        }

        UserAuthInfo userAuthInfo = BeanCopyUtils.copyProperties(UserAuthInfo.class, user);

        return userAuthInfo;
    }

    /**
     * get token from request
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        if (BaseUtil.isEmpty(request)) {
            return "";
        }

        String token = request.getHeader(Constants.TOKEN_NAME);

        if (BaseUtil.isEmpty(token)) {
            token = CookiesUtils.getCookieByName(request, Constants.TOKEN_NAME);
        }
        return token;
    }

}

