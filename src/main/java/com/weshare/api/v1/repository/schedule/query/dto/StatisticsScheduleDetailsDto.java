package com.weshare.api.v1.repository.schedule.query.dto;

public record StatisticsScheduleDetailsDto(
        int totalViewCount,
        int totalCommentCount,
        int totalLikeCount,
        long totalExpense
) {
}
