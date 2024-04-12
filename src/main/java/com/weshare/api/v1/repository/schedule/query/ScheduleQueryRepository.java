package com.weshare.api.v1.repository.schedule.query;

import com.weshare.api.v1.domain.schedule.Schedule;

import java.util.Optional;

public interface ScheduleQueryRepository {
    Optional<Schedule> findScheduleDetailById(Long scheduleId);
}
