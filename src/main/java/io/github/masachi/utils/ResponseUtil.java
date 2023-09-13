package io.github.masachi.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseUtil {

    public static void wrapResponse(HttpServletRequest request, HttpServletResponse response, Integer statusCode) {
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
    }

    public static void wrapResponse(HttpServletRequest request, HttpServletResponse response) {
        wrapResponse(request, response, 401);
    }
}
