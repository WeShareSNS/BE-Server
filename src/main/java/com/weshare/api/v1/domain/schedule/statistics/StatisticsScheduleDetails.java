package com.weshare.api.v1.domain.schedule.statistics;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsScheduleDetails extends BaseTimeEntity {
    @Id
    private Long id;
    @Column(unique = true, nullable = false)
    private Long scheduleId;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int totalViewCount;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int totalCommentCount;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long totalPrice;

    @Builder
    private StatisticsScheduleDetails(Long scheduleId, int totalViewCount, int totalCommentCount, long totalPrice) {
        this.scheduleId = scheduleId;
        this.totalViewCount = totalViewCount;
        this.totalCommentCount = totalCommentCount;
        this.totalPrice = totalPrice;
    }
}
