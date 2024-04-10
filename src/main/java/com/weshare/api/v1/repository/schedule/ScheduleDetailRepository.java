package com.weshare.api.v1.repository.schedule;

import com.weshare.api.v1.domain.schedule.Schedule;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleDetailRepository {
    Optional<Schedule> findScheduleDetailById(Long scheduleId);
}
