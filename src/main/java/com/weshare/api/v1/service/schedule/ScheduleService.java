package com.weshare.api.v1.service.schedule;

import com.weshare.api.v1.controller.schedule.ApplyScheduleRequest;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public void saveSchedule (ApplyScheduleRequest applyScheduleRequest) {
        scheduleRepository.save(applyScheduleRequest.toEntity());
    }
}
