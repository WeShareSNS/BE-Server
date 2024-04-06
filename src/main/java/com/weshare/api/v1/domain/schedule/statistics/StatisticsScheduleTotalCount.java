package com.weshare.api.v1.domain.schedule.statistics;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsScheduleTotalCount extends BaseTimeEntity {
    @Id
    private Long id;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long totalCount;

    public StatisticsScheduleTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
