package com.weshare.api.v1.service.schedule.query;

import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.repository.schedule.SchedulePageDto;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;


    public Page<SchedulePageDto> getSchedulePage(Pageable pageable) {
        return scheduleRepository.getSchedulePage(pageable);
    }
}
