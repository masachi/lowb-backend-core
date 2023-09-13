package io.github.masachi.filter.login;

import com.alibaba.fastjson.JSON;
import io.github.masachi.annotation.login.ClearLogin;
import io.github.masachi.annotation.login.NeedLogin;
import io.github.masachi.annotation.login.ParseUserInfo;
import io.github.masachi.data.CheckResult;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.ResponseUtil;
import io.github.masachi.utils.SpringBeanUtils;
import io.github.masachi.vo.RespVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Log4j2
@Order(1)
@DependsOn("checkUserService")
public class LoginFilter implements HandlerInterceptor {

    private static CheckUserHelp checkUserHelp;

    public LoginFilter() {
        checkUserHelp = SpringBeanUtils.getBean(CheckUserHelp.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // By-pass the static resource
        if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            log.debug("cat cast handler to HandlerMethod.class,this means this handel not a controller");
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // Quick return if the Application want to clear login
        ClearLogin clearLoginAnnotation = handlerMethod.getMethod().getAnnotation(ClearLogin.class);
        if (clearLoginAnnotation != null) {
            return true;
        }

        NeedLogin needLoginAnnotation = getNeedLoginAnnotation(handlerMethod);

        boolean needCheckPolicy = false;
        if (BaseUtil.isNotEmpty(needLoginAnnotation)) {
            needCheckPolicy = needLoginAnnotation.checkPolicy();
        } else {
            ParseUserInfo parseUserInfoAnnotation = getLoginUserAnnotation(handlerMethod);
            if (BaseUtil.isEmpty(parseUserInfoAnnotation)) {
                return true;
            }
        }

        CheckResult checkResult = checkUserHelp.checkTokenAndSecret(request, needCheckPolicy);
        if (BaseUtil.isEmpty(needLoginAnnotation) || checkResult.isSuccess()) {
            return true;
        }

        ResponseUtil.wrapResponse(request, response);
        response.getWriter().write(JSON.toJSONString(RespVO.error(checkResult.getMessage())));
        return false;
    }

    private NeedLogin getNeedLoginAnnotation(HandlerMethod handlerMethod) {
        NeedLogin needLoginAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(NeedLogin.class);
        if (needLoginAnnotation != null) {
            ClearLogin clearLoginAnnotation = handlerMethod.getMethod().getAnnotation(ClearLogin.class);
            if (clearLoginAnnotation != null) {
                needLoginAnnotation = null;
            }
            final NeedLogin methodNeedLoginAnnotation = handlerMethod.getMethod().getAnnotation(NeedLogin.class);
            if (methodNeedLoginAnnotation != null) {
                needLoginAnnotation = methodNeedLoginAnnotation;
            }
        } else {
            needLoginAnnotation = handlerMethod.getMethod().getAnnotation(NeedLogin.class);
        }
        return needLoginAnnotation;
    }

    private ParseUserInfo getLoginUserAnnotation(HandlerMethod handlerMethod) {
        ParseUserInfo parseUserInfoAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(ParseUserInfo.class);
        if (parseUserInfoAnnotation == null) {
            parseUserInfoAnnotation = handlerMethod.getMethod().getAnnotation(ParseUserInfo.class);
        }
        return parseUserInfoAnnotation;
    }
}
