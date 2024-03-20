package com.weshare.api.v1.controller.comment.dto;

import com.weshare.api.v1.service.comment.FindAllCommentDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

@Schema(description = "댓글 조회 응답 API")
public record FindAllCommentResponse(
        @Schema(title = "특정 여행일정의 모든 사용자 댓글 응답", description = "특정 여행일정의 모든 댓글을 확인할 수 있다.")
        List<FindAllCommentDto> comments,
        @Schema(title = "특정 여행일정의 댓글 개수", description = "특정 여행일정의 모든 댓글 개수를 응답한다.")
        int size
) {
    public FindAllCommentResponse {
        Objects.requireNonNull(comments);
        if (size < 0) {
            throw new IllegalStateException("개수가 올바르지 않습니다.");
        }
    }
}
