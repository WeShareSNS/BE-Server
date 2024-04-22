package com.weshare.api.v1.controller.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "댓글 생성 응답 API")
public record CreateChildCommentResponse(
        @Schema(title = "사용자의 댓글 id", description = "사용자의 특정 여행일정의 댓글을 등록할 수 있다.")
        Long commentId,
        @Schema(title = "사용자의 댓글 id", description = "사용자의 특정 여행일정의 댓글을 등록할 수 있다.")
        Long parentCommentId,
        @Schema(title = "댓글을 작성한 사용자 이름", description = "댓글을 남긴 사용자의 이름을 응답한다.")
        String commenterName,
        @Schema(title = "사용자가 등록한 댓글", description = "사용자가 등록한 댓글을 응답한다.")
        String content,
        @Schema(title = "사용자가 댓글을 등록한 시간", description = "사용자가 댓글을 등록한 시간을 알 수 있다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a", timezone = "Asia/Seoul", locale = "en_US")
        LocalDateTime createdDate
) {
    public CreateChildCommentResponse {
        Objects.requireNonNull(commentId);
        Objects.requireNonNull(parentCommentId);
        Objects.requireNonNull(createdDate);
        if (!StringUtils.hasText(commenterName)) {
            throw new IllegalStateException("유저 정보가 올바르지 않습니다.");
        }
        if (!StringUtils.hasText(content)) {
            throw new IllegalStateException("댓글이 올바르지 않습니다.");
        }
    }
}
