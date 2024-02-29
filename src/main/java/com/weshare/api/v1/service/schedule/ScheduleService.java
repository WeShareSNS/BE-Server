package com.weshare.api.v1.service.schedule;

import com.weshare.api.v1.controller.schedule.ApplyScheduleRequest;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public void saveSchedule(ApplyScheduleRequest applyScheduleRequest,
                             String userEmail) {

        User user = userRepository.findByEmail(userEmail).orElseThrow(IllegalAccessError::new);
        Schedule schedule = applyScheduleRequest.toEntity();
        schedule.setUser(user);
        scheduleRepository.save(schedule);
    }
}
