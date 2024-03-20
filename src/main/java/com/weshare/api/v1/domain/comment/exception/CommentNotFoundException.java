package com.weshare.api.v1.domain.comment.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super();
    }

    public CommentNotFoundException(String message) {
        super(message);
    }
}
