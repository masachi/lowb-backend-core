package io.github.masachi.annotation.policy;

import io.github.masachi.annotation.login.ClearLogin;
import io.github.masachi.annotation.login.NeedLogin;
import org.springframework.web.method.HandlerMethod;

public class CheckPolicyUtils {

    public static NeedCheckPolicy getNeedCheckPolicyAnnotation(HandlerMethod handlerMethod) {
        NeedCheckPolicy needCheckPolicyAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(NeedCheckPolicy.class);
        if (needCheckPolicyAnnotation != null) {
            ClearLogin clearLoginAnnotation = handlerMethod.getMethod().getAnnotation(ClearLogin.class);
            if (clearLoginAnnotation != null) {
                needCheckPolicyAnnotation = null;
            }
            final NeedCheckPolicy methodNeedLoginAnnotation = handlerMethod.getMethod().getAnnotation(NeedCheckPolicy.class);
            if (methodNeedLoginAnnotation != null) {
                needCheckPolicyAnnotation = methodNeedLoginAnnotation;
            }
        } else {
            needCheckPolicyAnnotation = handlerMethod.getMethod().getAnnotation(NeedCheckPolicy.class);
        }

        return needCheckPolicyAnnotation;
    }

}
