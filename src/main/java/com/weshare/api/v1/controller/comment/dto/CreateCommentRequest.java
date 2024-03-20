package com.weshare.api.v1.controller.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "댓글 등록 요청 API")
public record CreateCommentRequest (
        @Schema(title = "사용자 댓글", description = "사용자 댓글을 입력해주세요")
        @NotBlank(message = "댓글은 비어있을 수 없습니다.")
        String content){
}
