package com.weshare.api.v1.service.schedule.query.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.Set;

@Getter
public class ScheduleFilterPageDto {
    private final Long userId;
    private final String search;
    private final String expenseCondition;
    private final Set<String> destinations;
    private final Pageable pageable;

    @Builder
    private ScheduleFilterPageDto(Long userId, String search, String expenseCondition, Set<String> destinations, Pageable pageable) {
        this.userId = userId;
        this.search = search;
        this.expenseCondition = expenseCondition;
        this.destinations = destinations;
        this.pageable = pageable;
    }
}
