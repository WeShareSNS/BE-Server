package com.weshare.api.v1.controller.comment.dto;

import com.weshare.api.v1.domain.user.User;

import java.util.Objects;

public record UpdateCommentDto(
        User commenter,
        String content,
        Long scheduleId,
        Long commentId
) {
    public UpdateCommentDto {
        Objects.requireNonNull(commenter, "사용자가 존재하지 않습니다.");
        Objects.requireNonNull(scheduleId, "여행일정을 확인해주세요.");
        Objects.requireNonNull(commentId, "댓글을 확인해주세요.");
    }
}
