package com.weshare.api.v1.domain.user.exception;

public class UsernameDuplicateException extends RuntimeException{
    public UsernameDuplicateException(String message) {
        super(message);
    }
}
