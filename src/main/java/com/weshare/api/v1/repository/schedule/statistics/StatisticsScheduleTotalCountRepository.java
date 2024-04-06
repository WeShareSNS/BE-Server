package com.weshare.api.v1.repository.schedule.statistics;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleTotalCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsScheduleTotalCountRepository extends JpaRepository<StatisticsScheduleTotalCount, Long> {
}
