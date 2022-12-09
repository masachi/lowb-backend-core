package io.github.masachi.annotation.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.github.masachi.constant.Constants;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.EncryptUtils;
import io.github.masachi.utils.IpAddressUtil;
import io.github.masachi.utils.encryption.Des;
import io.github.masachi.vo.RespVO;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Log4j2
@Component
@DependsOn("springBeanUtils")
public class LoggableRestControllerHelper {

    @Autowired(required = false)
    private HttpServletRequest request;

    /**
     * 通過注解定义切点
     */
    @Pointcut("@within(io.github.masachi.annotation.aspect.LoggableRestController)")
    public void pointCut() {
    }

    /**
     * <p>使用AOP统一封装controller返回结果</p>
     *
     * @param point the date to format, not null
     * @return the object
     * @throws Throwable 系统异常
     */
    @ResponseBody
    @Around("pointCut() && @within(io.github.masachi.annotation.aspect.LoggableRestController)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object respondData = point.proceed();

        // If the API return a RespVO object,
        // Return it directly here to avoid encode the result.
        // Currently, this is a workaround for /version API
        if (respondData instanceof RespVO) {
            String json = JSONObject.toJSONString(respondData, SerializerFeature.DisableCircularReferenceDetect);
            return json;
        }

        RespVO res = RespVO.success(respondData);
        // 是否采用加密 不是localhost 就加密
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String needEncryptHeader = request.getHeader(Constants.RES_ENCRYPT);
        String ip = IpAddressUtil.getIpAdrress(request);

        if("[0:0:0:0:0:0:0:1]".equals(ip) || "127.0.0.1".equals(ip)) {
            res.setEncrypted(false);
        }

        if("false".equalsIgnoreCase(needEncryptHeader)) {
            res.setEncrypted(false);
        }

        // 是否强制加密
        if("true".equalsIgnoreCase(needEncryptHeader)) {
            res.setEncrypted(true);
        }

        if(res.isEncrypted()) {
            encryptResponse(respondData, res);
        }

        return JSONObject.toJSONString(res);
    }

    public void encryptResponse(Object respondData, RespVO res) {
        String encryptResponseDataHeader = request.getHeader(Constants.RES_ENCRYPT);
        if(BaseUtil.isEmpty(encryptResponseDataHeader) || "true".equalsIgnoreCase(encryptResponseDataHeader)) {
            EncryptUtils encryptUtils = new EncryptUtils(new Des());
            String encode = encryptUtils.encode(JSON.toJSONString(respondData));
            res.setData(encode);
        }
    }
}