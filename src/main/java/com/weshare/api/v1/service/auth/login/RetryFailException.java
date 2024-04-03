package com.weshare.api.v1.service.auth.login;

public class RetryFailException extends RuntimeException{
    public RetryFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
