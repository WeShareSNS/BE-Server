package com.weshare.api.v1.repository.schedule;

import com.weshare.api.v1.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {
}
