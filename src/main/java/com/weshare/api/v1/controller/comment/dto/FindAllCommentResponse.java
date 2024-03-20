package com.weshare.api.v1.controller.comment.dto;

import com.weshare.api.v1.service.comment.FindAllCommentDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

@Schema(description = "댓글 조회 응답 API")
public record FindAllCommentResponse(
        // scheduleId값도 넣어주자
        @Schema(title = "특정 여행일정의 모든 사용자 댓글 응답", description = "사용자 특정 여행일정의 댓글을 확인할 수 있다. ")
        List<FindAllCommentDto> comments,
        @Schema(title = "특정 게시물의 모든 사용자 댓글", description = "사용자 댓글을 입력해주세요")
        int size
) {
    public FindAllCommentResponse {
        Objects.requireNonNull(comments);
        if (size < 0) {
            throw new IllegalStateException("개수가 올바르지 않습니다.");
        }
    }
}
