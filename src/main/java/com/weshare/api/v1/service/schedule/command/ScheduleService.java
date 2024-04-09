package com.weshare.api.v1.service.schedule.command;

import com.weshare.api.v1.controller.schedule.command.dto.CreateScheduleDto;
import com.weshare.api.v1.controller.schedule.command.dto.UpdateScheduleDto;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.event.schedule.ScheduleCreatedEvent;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.service.exception.AccessDeniedModificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Schedule saveSchedule(final CreateScheduleDto createScheduleDto) {
        Schedule schedule = createScheduleDto.toEntity();
        Schedule save = scheduleRepository.save(schedule);
        eventPublisher.publishEvent(new ScheduleCreatedEvent(schedule.getId(), schedule.getTotalScheduleExpense()));
        return save;
    }

    public void updateSchedule(UpdateScheduleDto updateScheduleDto) {
        final Schedule updateSchedule = updateScheduleDto.toEntity();
        final Schedule findSchedule = scheduleRepository.findById(updateSchedule.getId())
                .orElseThrow(ScheduleNotFoundException::new);

        User user = findSchedule.getUser();
        if (!user.isSameUser(updateSchedule.getUser())) {
            throw new AccessDeniedModificationException();
        }


    }
}
