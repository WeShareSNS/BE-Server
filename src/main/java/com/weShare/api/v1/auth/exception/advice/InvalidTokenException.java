package com.weShare.api.v1.auth.exception.advice;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
