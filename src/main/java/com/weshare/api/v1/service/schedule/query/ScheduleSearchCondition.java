package com.weshare.api.v1.service.schedule.query;

import io.jsonwebtoken.lang.Assert;
import org.springframework.data.domain.Pageable;

public record ScheduleSearchCondition(
        Long userId,
        String search,
        Pageable pageable
) {
    public ScheduleSearchCondition {
        Assert.hasText(search, "검색 조건은 비어있거나 공백일 수 없습니다.");
    }
}
