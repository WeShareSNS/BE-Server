package com.weshare.api.v1.controller.like;

import com.weshare.api.v1.service.like.FindAllScheduleLikeDto;

import java.util.List;
import java.util.Objects;

public record FindAllScheduleLikeResponse(
        List<FindAllScheduleLikeDto> likes,
        int size
) {
    public FindAllScheduleLikeResponse {
        Objects.requireNonNull(likes);
        if (size < 0) {
            throw new IllegalStateException("좋아요 개수가 올바르지 않습니다.");
        }
    }
}
