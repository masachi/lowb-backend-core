package io.github.masachi.exceptions;

import lombok.Getter;

@Getter
public class GeneralException extends Error {

    public int code;

    public GeneralException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GeneralException(String message) {
        super(message);
        this.code = 10000;
    }
}
