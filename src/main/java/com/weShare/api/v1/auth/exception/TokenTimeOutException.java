package com.weShare.api.v1.auth.exception;

public class TokenTimeOutException extends RuntimeException{
    public TokenTimeOutException(String message) {
        super(message);
    }
}
