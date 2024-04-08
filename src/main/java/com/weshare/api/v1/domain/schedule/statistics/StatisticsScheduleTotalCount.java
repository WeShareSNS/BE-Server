package com.weshare.api.v1.domain.schedule.statistics;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class StatisticsScheduleTotalCount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long totalCount;

    public StatisticsScheduleTotalCount() {
    }

    public void incrementTotalCount() {
        totalCount += 1;
    }

    public void syncScheduleTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

}
