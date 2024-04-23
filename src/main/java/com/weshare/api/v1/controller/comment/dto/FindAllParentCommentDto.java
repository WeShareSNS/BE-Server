package com.weshare.api.v1.controller.comment.dto;

import org.springframework.data.domain.Pageable;

public record FindAllParentCommentDto(
        Long userId,
        Long scheduleId,
        Pageable pageable
) {
}
