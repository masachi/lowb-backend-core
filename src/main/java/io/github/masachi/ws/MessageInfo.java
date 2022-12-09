package io.github.masachi.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageInfo {

    @JsonProperty("message_id")
    public String messageId;

    @JsonProperty("message_type")
    public MessageType messageType;

    @JsonProperty("message")
    public String message;

    @JsonProperty("conversation_id")
    public String conversationId;

    @JsonProperty("send_time")
    public String sendTime;

    @JsonProperty("send_by")
    public String sendBy;

    @JsonProperty("file")
    public String file;

    @JsonProperty("uri")
    public String uri;
}
