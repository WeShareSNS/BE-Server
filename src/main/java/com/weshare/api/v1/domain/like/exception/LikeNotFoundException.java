package com.weshare.api.v1.domain.like.exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException() {
        super();
    }

    public LikeNotFoundException(String message) {
        super(message);
    }
}
