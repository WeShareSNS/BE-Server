package com.weshare.api.v1.controller.comment.dto;

import org.springframework.data.domain.Pageable;

public record FindAllChildCommentDto(
        Long userId,
        Long scheduleId,
        Long parentCommentId,
        Pageable pageable
) {
}
