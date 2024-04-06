package com.weshare.api.v1.repository.schedule.statistics;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsScheduleDetailsRepository extends JpaRepository<StatisticsScheduleDetails, Long> {
    Optional<StatisticsScheduleDetails> findByScheduleId(Long scheduleId);
}
