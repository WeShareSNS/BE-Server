package com.weshare.api.v1.domain.schedule.statistics;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsScheduleDetails extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private Long scheduleId;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int totalViewCount;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int totalCommentCount;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int totalLikeCount;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long totalExpense;

    @Builder
    private StatisticsScheduleDetails(Long scheduleId, int totalViewCount, int totalCommentCount, int totalLikeCount, long totalExpense) {
        this.scheduleId = scheduleId;
        this.totalViewCount = totalViewCount;
        this.totalCommentCount = totalCommentCount;
        this.totalLikeCount = totalLikeCount;
        this.totalExpense = totalExpense;
    }

    public StatisticsScheduleDetails(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public StatisticsScheduleDetails(int totalViewCount, int totalCommentCount, int totalLikeCount, long totalExpense) {
        this.totalViewCount = totalViewCount;
        this.totalCommentCount = totalCommentCount;
        this.totalLikeCount = totalLikeCount;
        this.totalExpense = totalExpense;
    }
}
