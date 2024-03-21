package com.weshare.api.v1.service.like;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public record CreateLikeResponse(
        Long likeId,
        String likerName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime likedTime
) {
    public CreateLikeResponse {
        Objects.requireNonNull(likeId);
        if (!StringUtils.hasText(likerName)) {
            throw new IllegalStateException("사용자가 올바르지 않습니다.");
        }
        Objects.requireNonNull(likedTime);
    }
}
