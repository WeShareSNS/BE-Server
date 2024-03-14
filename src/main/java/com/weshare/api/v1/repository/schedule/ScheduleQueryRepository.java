package com.weshare.api.v1.repository.schedule;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleQueryRepository {
    Page<SchedulePageDto> getSchedulePage(Pageable pageable);
}
