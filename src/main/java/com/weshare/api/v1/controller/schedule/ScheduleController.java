package com.weshare.api.v1.controller.schedule;

import com.weshare.api.v1.service.schedule.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/schedule")
    public ResponseEntity saveSchedule(@Valid @RequestBody ApplyScheduleRequest applyScheduleRequest) {
        scheduleService.saveSchedule(applyScheduleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
