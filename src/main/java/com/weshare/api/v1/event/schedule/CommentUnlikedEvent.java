package com.weshare.api.v1.event.schedule;

import java.util.Objects;

public record CommentUnlikedEvent(
    Long commentId
) {
    public CommentUnlikedEvent {
        Objects.requireNonNull(commentId);
    }
}
