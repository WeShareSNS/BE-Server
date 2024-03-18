package com.weshare.api.v1.controller.schedule.command;

import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.schedule.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "schedule-command-controller", description = "여행일정 등록 컨트롤러")
@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "여행일정 등록 API", description = "기본값으로 12개 기준으로 pagination이 적용되며 최신글으로 정렬됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행일정 등록 성공")
    })
    @PostMapping("/schedule")
    public ResponseEntity saveSchedule(@Valid @RequestBody CreateScheduleRequest createScheduleRequest,
                                       @AuthenticationPrincipal User user) {
        CreateScheduleDto createScheduleDto = CreateScheduleDto.from(createScheduleRequest);
        scheduleService.saveSchedule(createScheduleDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
