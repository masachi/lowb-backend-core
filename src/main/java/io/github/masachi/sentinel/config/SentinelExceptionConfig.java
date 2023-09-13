package io.github.masachi.sentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.fastjson.JSON;
import io.github.masachi.utils.ResponseUtil;
import io.github.masachi.vo.RespVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.io.PrintWriter;

@Configuration
public class SentinelExceptionConfig {

    @Bean
    public BlockExceptionHandler sentinelBlockExceptionHandler() {
        return (request, response, e) -> {
            RespVO sentinelBlockResponse = RespVO.error("请求次数过多，请稍后重试");

            // 429 Too Many Requests
            ResponseUtil.wrapResponse(request, response, 429);
            response.getWriter().write(JSON.toJSONString(sentinelBlockResponse));
            response.getWriter().flush();
        };
    }
}
