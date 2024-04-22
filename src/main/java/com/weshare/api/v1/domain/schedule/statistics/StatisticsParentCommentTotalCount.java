package com.weshare.api.v1.domain.schedule.statistics;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsParentCommentTotalCount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long parentCommentId;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long totalCount;

    public StatisticsParentCommentTotalCount(Long parentCommentId, long totalCount) {
        this.parentCommentId = parentCommentId;
        this.totalCount = totalCount;
    }

    public void incrementTotalCount() {
        totalCount += 1;
    }

    public void decrementTotalCount() {
        if (totalCount <= 0) {
            throw new IllegalStateException("총 카운트 수는 음수일 수 없습니다.");
        }
        totalCount -= 1;
    }

    public void syncScheduleTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

}
