package com.fwea.loopychat;

import lombok.Getter;

public class AppException extends RuntimeException {
    @Getter
    final private int code;

    public AppException(String message) {
        this(message, 400);
    }

    public AppException(String message, int code) {
        super(message);
        this.code = code;
    }
}
