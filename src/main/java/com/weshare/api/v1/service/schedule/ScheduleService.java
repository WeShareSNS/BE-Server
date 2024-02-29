package com.weshare.api.v1.service.schedule;

import com.weshare.api.v1.controller.schedule.CreateScheduleRequest;
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

    public void saveSchedule(CreateScheduleRequest createScheduleRequest,
                             String userEmail) {

        User user = userRepository.findByEmail(userEmail).orElseThrow(IllegalAccessError::new);
        Schedule schedule = createScheduleRequest.toEntity();
        schedule.setUser(user);
        scheduleRepository.save(schedule);
    }
}
