package io.github.masachi.exceptions;

import lombok.Getter;

@Getter
public class ServerErrorException extends Error {

    private int severity;

    private String title;

    public ServerErrorException(int severity, String message, String title) {
        super(message);
        this.severity = severity;
        this.title = title;
    }
}
