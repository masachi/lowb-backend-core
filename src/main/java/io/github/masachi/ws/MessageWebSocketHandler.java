package io.github.masachi.ws;

import com.alibaba.fastjson.JSON;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class MessageWebSocketHandler extends TextWebSocketHandler {
    private Set<WebSocketSession> listenerSessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        listenerSessions.add(session);
        System.out.println("WebSocket Connection Established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        // TODO 业务消息处理 可参照 https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-25/lab-websocket-25-02/src/main/java/cn/iocoder/springboot/lab25/springwebsocket
        System.out.println(JSON.parseObject(message.getPayload(), MessageInfo.class));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        listenerSessions.remove(session);
        System.out.println("WebSocket Connection Closed");
    }
}
