package com.weshare.api.v1.event.schedule;

import java.util.Objects;

public record CommentCreatedEvent (
        Long scheduleId,
        Long parentCommentId
){
    public CommentCreatedEvent {
        Objects.requireNonNull(scheduleId);
    }
}
