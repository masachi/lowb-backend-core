package io.github.masachi.config;

import io.github.masachi.condition.WebSocketConditional;
import io.github.masachi.ws.MessageWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.ServletContext;

@Configuration
@EnableWebSocket
@Conditional(WebSocketConditional.class)
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    ServletContext servletContext;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MessageWebSocketHandler(), "/ws/chat")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}
