package com.weshare.api.v1.controller.schedule.command;

import com.weshare.api.v1.controller.schedule.command.dto.CreateScheduleDto;
import com.weshare.api.v1.controller.schedule.command.dto.CreateScheduleRequest;
import com.weshare.api.v1.controller.schedule.command.dto.UpdateScheduleDto;
import com.weshare.api.v1.controller.schedule.command.dto.UpdateScheduleRequest;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.schedule.command.ScheduleService;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "schedule-command-controller", description = "여행일정 등록 컨트롤러")
@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "여행일정 등록 API", description = "기본값으로 12개 기준으로 pagination이 적용되며 최신글으로 정렬됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행일정 등록 성공"),
            @ApiResponse(responseCode = "400", description = "요청을 확인해주세요")
    })
    @PostMapping("/schedules")
    public ResponseEntity saveSchedule(@Valid @RequestBody CreateScheduleRequest createScheduleRequest,
                                       @AuthenticationPrincipal User user) {
        CreateScheduleDto createScheduleDto = CreateScheduleDto.of(createScheduleRequest, user);
        scheduleService.saveSchedule(createScheduleDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "여행일정 수정 API", description = "여행일정 id와 여행일정의 기존 데이터들이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행일정 수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청을 확인해주세요"),
            @ApiResponse(responseCode = "403", description = "사용자를 확인해주세요"),
    })
    @PatchMapping("/schedules")
    public ResponseEntity updateSchedule(@Valid @RequestBody UpdateScheduleRequest updateScheduleRequest,
                                       @AuthenticationPrincipal User user) {
        UpdateScheduleDto updateScheduleDto = UpdateScheduleDto.of(updateScheduleRequest, user);
        scheduleService.updateSchedule(updateScheduleDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
