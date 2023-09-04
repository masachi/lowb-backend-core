package io.github.masachi.filter.audit;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.github.masachi.annotation.login.ParseUserInfo;
import io.github.masachi.constant.Constants;
import io.github.masachi.data.UserInfo;
import io.github.masachi.utils.IpAddressUtil;
import io.github.masachi.utils.kafka.KafkaMessage;
import io.github.masachi.utils.kafka.impl.KafkaProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Log4j2
@Order(6)
public class ControllerAuditInterceptor implements HandlerInterceptor {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[ControllerAuditInterceptor][preHandle]start");
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //获取登录的用户
        UserInfo userInfo = (UserInfo) request.getAttribute(Constants.REQUEST_USER_AUTH_INFO);
        if (userInfo == null) {
            log.info("[ControllerAuditInterceptor][preHandle] userInfo is null");
            return true;
        } else {
            log.info("[ControllerAuditInterceptor][preHandle] userInfo :{}", userInfo);
        }

        AuditLog auditLog = AuditLog.builder()
                .traceId(NanoIdUtils.randomNanoId())
                .userId(userInfo.getId())
                .clientIp(IpAddressUtil.getIpAdrress(request))
                .method(request.getMethod())
                .requestPathname(request.getRequestURI())
                .requestTime(new Date())
                .build();

        // kafka放出去 TODO
        kafkaProducer.sendMessage(
                KafkaMessage.builder()
                        .message(auditLog)
                        .build()
        );

        return true;
    }

    private ParseUserInfo getAuditLogAnnotation(HandlerMethod handlerMethod) {
        ParseUserInfo parseUserInfoAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(ParseUserInfo.class);
        if (parseUserInfoAnnotation == null) {
            parseUserInfoAnnotation = handlerMethod.getMethod().getAnnotation(ParseUserInfo.class);
        }
        return parseUserInfoAnnotation;
    }
}
