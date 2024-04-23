package com.weshare.api.v1.domain.schedule.like.exception;

public class CommentLikeNotFoundException extends RuntimeException {
    public CommentLikeNotFoundException() {
        super();
    }

    public CommentLikeNotFoundException(String message) {
        super(message);
    }
}
