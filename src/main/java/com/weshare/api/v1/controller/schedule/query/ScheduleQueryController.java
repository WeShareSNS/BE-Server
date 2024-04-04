package com.weshare.api.v1.controller.schedule.query;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.service.schedule.query.dto.SchedulePageDto;
import com.weshare.api.v1.service.schedule.query.ScheduleQueryService;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "schedule-query-controller", description = "여행일정 조회 컨트롤러")
@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class ScheduleQueryController {

    private final ScheduleQueryService scheduleQueryService;
    private final Response response;

    @Operation(summary = "여행일정 전체 조회 API", description = "기본값으로 12개 기준으로 pagination이 적용되며 최신글으로 정렬됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행일정 조회 성공")
    })
    @GetMapping("/schedules")
    public Page<SchedulePageDto> getSchedule(
            @PageableDefault(size = 12, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return scheduleQueryService.getSchedulePage(pageable);
    }

    @Operation(summary = "여행일정 상세보기 API", description = "특정 ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행일정 조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회하는 여행일정이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "여행일정이 존재하지 않습니다.")
    })
    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetails(@PathVariable Long scheduleId) {
        ScheduleDetailDto scheduleDetails = scheduleQueryService.getScheduleDetails(scheduleId);
        return response.success(scheduleDetails);
    }
}
