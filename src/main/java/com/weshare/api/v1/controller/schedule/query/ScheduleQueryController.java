package com.weshare.api.v1.controller.schedule.query;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.schedule.ViewCountManager;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.schedule.query.FindScheduleDetailDto;
import com.weshare.api.v1.service.schedule.query.ScheduleQueryService;
import com.weshare.api.v1.service.schedule.query.ScheduleSearchCondition;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleDetailDto;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleFilterPageDto;
import com.weshare.api.v1.service.schedule.query.dto.SchedulePageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Tag(name = "schedule-query-controller", description = "여행일정 조회 컨트롤러")
@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class ScheduleQueryController {

    private final ViewCountManager viewCountManager;
    private final ScheduleQueryValidator validator;
    private final ScheduleQueryService scheduleQueryService;
    private final Response response;

    @Operation(summary = "여행일정 전체 조회 API", description = "기본값으로 12개 기준으로 pagination이 적용되며 최신글으로 정렬됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행일정 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 요청을 확인해주세요")
    })
    @GetMapping("/schedules")
    public Page<SchedulePageDto> getSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String expense,
            @RequestParam(name = "destination", required = false) Set<String> destinations,
            @PageableDefault(size = 12, sort = "created-date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        validator.validateExpenseCondition(expense);

        ScheduleFilterPageDto scheduleFilterPageDto = createScheduleFilterPageDto(user, expense, destinations, pageable);
        return scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
    }

    private ScheduleFilterPageDto createScheduleFilterPageDto(
            User user,
            String expense,
            Set<String> destinations,
            Pageable pageable
    ) {
        return ScheduleFilterPageDto.builder()
                .userId(user == null ? null : user.getId())
                .expenseCondition(expense)
                .destinations(destinations)
                .pageable(pageable)
                .build();
    }

    @Operation(summary = "여행일정 검색 조회 API", description = "특정 키워드를 통해서 제목과 일치한 글을 조회할 수 있습니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "여행일정 검색 성공")})
    @GetMapping("/schedules/search")
    public Page<SearchScheduleDto> searchSchedule(
            @PageableDefault(size = 12, sort = "created-date", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("q") String search,
            @AuthenticationPrincipal User user
    ) {
        ScheduleSearchCondition scheduleSearchCondition = createScheduleSearchCondition(pageable, search, user);
        return scheduleQueryService.searchSchedule(scheduleSearchCondition);
    }

    private static ScheduleSearchCondition createScheduleSearchCondition(Pageable pageable, String search, User user) {
        final Long userId = user == null ? null : user.getId();
        return new ScheduleSearchCondition(userId, search, pageable);
    }

    @Operation(summary = "여행일정 상세보기 API", description = "특정 여행 일정을 상세조회할 수 있으며 조회 수를 기록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행일정 조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회하는 여행일정이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "여행일정이 존재하지 않습니다.")
    })
    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetails(
            @AuthenticationPrincipal User user,
            @PathVariable Long scheduleId,
            HttpServletRequest request,
            HttpServletResponse servletResponse
    ) {
        final FindScheduleDetailDto findScheduleDetailDto =
                new FindScheduleDetailDto(scheduleId, Optional.ofNullable(user).map(User::getId));
        final ScheduleDetailDto scheduleDetails = scheduleQueryService.getScheduleDetails(findScheduleDetailDto);

        /* ip + mac address로 redis로 조회수 중복을 관리할 수 있지만 간단하게 쿠키로 해결 */
        viewCountManager.viewCountUp(scheduleId, request, servletResponse);
        return response.success(scheduleDetails);
    }

}
