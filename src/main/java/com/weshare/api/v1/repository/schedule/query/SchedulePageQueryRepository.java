package com.weshare.api.v1.repository.schedule.query;


import com.weshare.api.v1.repository.schedule.query.dto.SchedulePageFlatDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulePageQueryRepository {
    Page<SchedulePageFlatDto> findSchedulePage(Pageable pageable);
}
