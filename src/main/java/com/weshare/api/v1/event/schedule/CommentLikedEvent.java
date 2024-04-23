package com.weshare.api.v1.event.schedule;

import java.util.Objects;

public record CommentLikedEvent(
    Long commentId
) {
    public CommentLikedEvent {
        Objects.requireNonNull(commentId);
    }
}
