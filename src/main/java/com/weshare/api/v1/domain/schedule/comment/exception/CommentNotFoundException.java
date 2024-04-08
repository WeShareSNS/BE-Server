package com.weshare.api.v1.domain.schedule.comment.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super();
    }

    public CommentNotFoundException(String message) {
        super(message);
    }
}
