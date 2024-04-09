package com.weshare.api.v1.service.exception;

public class AccessDeniedModificationException extends RuntimeException {
    public AccessDeniedModificationException() {
    }

    public AccessDeniedModificationException(String message) {
        super(message);
    }

    public AccessDeniedModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
