package com.weshare.api.v1.repository.schedule.query;

import com.weshare.api.v1.repository.schedule.query.dto.ScheduleDetailDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleDetailQueryRepository {
    List<ScheduleDetailDto> findScheduleDetail(Long scheduleId);
}
