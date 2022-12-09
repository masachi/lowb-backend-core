package io.github.masachi.filter.encrypt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.masachi.exceptions.InvalidateArgumentException;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.EncryptUtils;
import io.github.masachi.utils.encryption.Des;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


@Log4j2
public class EncryptRequestWrapper extends HttpServletRequestWrapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final EncryptUtils encryptUtils = new EncryptUtils(new Des());

    private HttpServletRequest request;


    /**
     * Creates a ServletRequest adaptor wrapping the given request object.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public EncryptRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public Map getParameterMap() {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (BaseUtil.isNotEmpty(parameterMap)) {
            parameterMap.forEach((k, v) -> {
                try {
                    v[0] = encryptUtils.decode(v[0]);
                    parameterMap.put(k, v);
                } catch (Exception e) {
                    log.error("解密失败!!!,请求url: "+request.getRequestURL()+" , "+v[0]);
                    throw new InvalidateArgumentException(1,"参数异常");
                }
            });
        }
        return parameterMap;
    }


    @Override
    public ServletInputStream getInputStream() {
        Object value;
        try {
            value = objectMapper.readValue(request.getInputStream(), Object.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(JSON.toJSONString("").getBytes(Charset.forName("UTF-8")));
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }

                @Override
                public int read() {
                    return inputStream.read();
                }
            };
        }
        LinkedHashMap<String, String> param = new LinkedHashMap();
        if (BaseUtil.isNotEmpty(value)) {
            param = (LinkedHashMap) value;
            for (String key : param.keySet()) {
                try {
                    param.put(key, encryptUtils.decode(param.get(key)));
                } catch (Exception e) {
                    log.error("解密失败!!! ,请求url: "+request.getRequestURL()+" ,参数 {} "+ JSONObject.toJSONString(param));
                    throw new InvalidateArgumentException(1,"参数异常");
                }
            }
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(JSON.toJSONString(param).getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return inputStream.read();
            }
        };
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] v = super.getParameterValues(name);
        if (BaseUtil.isEmpty(v)) {
            return new String[]{};
        }

        String s = v[0];
        if (BaseUtil.isEmpty(s)) {
            return new String[]{};
        }

        try {
            v[0] = encryptUtils.decode(s);
        } catch (Exception e) {
        }
        return v;
    }
}
