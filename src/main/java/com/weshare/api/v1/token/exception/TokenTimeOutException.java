package com.weshare.api.v1.token.exception;

public class TokenTimeOutException extends RuntimeException{
    public TokenTimeOutException(String message) {
        super(message);
    }
}
