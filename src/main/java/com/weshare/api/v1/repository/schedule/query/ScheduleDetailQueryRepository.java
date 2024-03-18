package com.weshare.api.v1.repository.schedule.query;

import com.weshare.api.v1.domain.schedule.Schedule;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleDetailQueryRepository {
    Schedule findScheduleDetail(Long scheduleId);
}
