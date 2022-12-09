package io.github.masachi.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookiesUtils {

    public static String getCookieByName(HttpServletRequest httpRequest, String name) {
        if (BaseUtil.isEmpty(httpRequest.getCookies())) {
            return "";
        }

        for (Cookie cookie : httpRequest.getCookies()) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }

        return "";
    }
}
