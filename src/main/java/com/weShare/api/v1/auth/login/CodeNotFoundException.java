package com.weShare.api.v1.auth.login;

public class CodeNotFoundException extends RuntimeException{
    public CodeNotFoundException(String message) {
        super(message);
    }
}
