package com.weshare.api.v1.controller.schedule.command;

import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.schedule.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/schedule")
    public ResponseEntity saveSchedule(@Valid @RequestBody CreateScheduleRequest createScheduleRequest,
                                       @AuthenticationPrincipal User user) {
        CreateScheduleDto createScheduleDto = CreateScheduleDto.from(createScheduleRequest);
        scheduleService.saveSchedule(createScheduleDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
