package com.weshare.api.v1.service.auth.login;

public class NotUniqueNameException extends RuntimeException {
    public NotUniqueNameException() {
        super();
    }

    public NotUniqueNameException(String message) {
        super(message);
    }
}
