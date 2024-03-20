package com.weshare.api.v1.service.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "특정 여행일정의 모든 사용자 댓글")
public record FindAllCommentDto(
        @Schema(title = "사용자가 등록한 댓글 id", description = "사용자가 등록한 댓글 id를 응답한다.")
        Long commentId,
        @Schema(title = "댓글을 남긴 사용자 이름", description = "댓글을 남긴 사용자의 이름을 응답한다.")
        String commenterName,
        @Schema(title = "사용자가 남긴 댓글 내용", description = "사용자가 남긴 댓글 내용을 응답한다.")
        String content,
        @Schema(title = "사용자가 댓글을 작성한 시간", description = "사용자 댓글을 작성한 시간을 알 수 있다..")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdDate
) {
    public FindAllCommentDto {
        Objects.requireNonNull(commentId);
        Objects.requireNonNull(createdDate);
        if (!StringUtils.hasText(commenterName)) {
            throw new IllegalStateException("사용자 정보가 올바르지 않습니다.");
        }
        if (!StringUtils.hasText(content)) {
            throw new IllegalStateException("댓글이 올바르지 않습니다.");
        }
    }
}
