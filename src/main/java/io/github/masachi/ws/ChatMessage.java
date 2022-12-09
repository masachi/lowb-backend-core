package io.github.masachi.ws;

import com.alibaba.fastjson.JSON;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.WebSocketMessage;

import java.nio.charset.StandardCharsets;

public class ChatMessage implements WebSocketMessage<MessageInfo> {

    private MessageInfo messageInfo;

    private boolean last;

    public ChatMessage(MessageInfo payload) {
        this.messageInfo = payload;
    }

    @Override
    public MessageInfo getPayload() {
        return this.messageInfo;
    }

    public int getPayloadLength() {
        return this.asBytes().length;
    }

    @Override
    public boolean isLast() {
        return this.last;
    }

    public byte[] asBytes() {
        return JSON.toJSONString(this.getPayload()).getBytes(StandardCharsets.UTF_8);
    }
}
