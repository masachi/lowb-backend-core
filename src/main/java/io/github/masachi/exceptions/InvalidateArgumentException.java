package io.github.masachi.exceptions;

import lombok.Getter;

@Getter
public class InvalidateArgumentException extends Error {

    private int code;

    public InvalidateArgumentException(int code, String message) {
        super(message);
        this.code = code;
    }
}
