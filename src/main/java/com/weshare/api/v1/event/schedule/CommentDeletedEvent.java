package com.weshare.api.v1.event.schedule;

import java.util.Objects;

public record CommentDeletedEvent(
        Long scheduleId,
        Long commentId,
        Long parentCommentId,
        int deletedCommentCount
) {
    public CommentDeletedEvent {
        Objects.requireNonNull(scheduleId);
        Objects.requireNonNull(commentId);
    }
}
