package com.weShare.api.v1.domain.user.exception;

public class EmailDuplicateException extends RuntimeException{
    public EmailDuplicateException(String message) {
        super(message);
    }
}
