package com.weshare.api.v1.service.schedule.query;

import com.weshare.api.v1.controller.schedule.query.SearchScheduleDto;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.repository.schedule.query.ExpenseCondition;
import com.weshare.api.v1.repository.schedule.query.SchedulePageQueryRepository;
import com.weshare.api.v1.repository.schedule.query.ScheduleQueryRepository;
import com.weshare.api.v1.repository.schedule.query.dto.ScheduleConditionPageDto;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleDetailDto;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleFilterPageDto;
import com.weshare.api.v1.service.schedule.query.dto.SchedulePageDto;
import com.weshare.api.v1.service.schedule.query.dto.UserScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final SchedulePageQueryRepository pageQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleRepository scheduleRepository;

    public Page<SchedulePageDto> getSchedulePage(ScheduleFilterPageDto scheduleFilterPageDto) {

        ScheduleConditionPageDto scheduleConditionPageDto = getScheduleConditionPageDto(scheduleFilterPageDto);
        final Page<Schedule> schedulePage = pageQueryRepository.findSchedulePage(scheduleConditionPageDto);
        final List<Long> scheduleIds = getScheduleIds(schedulePage);

        final Map<Long, StatisticsScheduleDetails> statisticsDetailsScheduleIdMap = pageQueryRepository.findStatisticsDetailsScheduleIdMap(scheduleIds);
        final Map<Long, Boolean> likedSchedulesMap = pageQueryRepository.findLikedSchedulesMap(scheduleIds, scheduleConditionPageDto.getUserId());

        return schedulePage.map(s -> convertSchedulePageDto(s, statisticsDetailsScheduleIdMap, likedSchedulesMap));
    }
    private ScheduleConditionPageDto getScheduleConditionPageDto(ScheduleFilterPageDto scheduleFilterPageDto) {
        final List<Destination> destinations = getDestinations(scheduleFilterPageDto.getDestinations());
        ExpenseCondition expenseCondition = ExpenseCondition.convert(scheduleFilterPageDto.getExpenseCondition());

        return ScheduleConditionPageDto.builder()
                .userId(scheduleFilterPageDto.getUserId())
                .destinations(destinations)
                .expenseCondition(expenseCondition)
                .pageable(scheduleFilterPageDto.getPageable())
                .build();
    }

    private List<Destination> getDestinations(Set<String> destinations) {
        if (destinations == null || destinations.isEmpty()) {
            return List.of(Destination.EMPTY);
        }
        return destinations.stream()
                .map(Destination::findDestinationByName)
                .toList();
    }

    private List<Long> getScheduleIds(Page<Schedule> schedulePage) {
        return schedulePage.getContent().stream()
                .map(Schedule::getId)
                .toList();
    }

    private SchedulePageDto convertSchedulePageDto(
            Schedule schedule,
            Map<Long, StatisticsScheduleDetails> statisticsScheduleDetailsMap,
            Map<Long, Boolean> likedSchedulesMap
    ) {
        final Long scheduleId = schedule.getId();
        final StatisticsScheduleDetails statisticsScheduleDetails = statisticsScheduleDetailsMap.get(scheduleId);

        return SchedulePageDto.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .destination(schedule.getDestination())
                .expense(statisticsScheduleDetails.getTotalExpense())
                .userName(schedule.getUser().getName())
                .likesCount(statisticsScheduleDetails.getTotalLikeCount())
                .commentsCount(statisticsScheduleDetails.getTotalCommentCount())
                .viewCount(statisticsScheduleDetails.getTotalViewCount())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .createdDate(LocalDate.from(schedule.getCreatedDate()))
                .isLiked(likedSchedulesMap.get(scheduleId))
                .build();
    }

    public ScheduleDetailDto getScheduleDetails(Long scheduleId) {
        if (scheduleId == null) {
            throw new IllegalArgumentException("게시물에 접근할 수 없습니다.");
        }
        final Schedule scheduleDetail = scheduleQueryRepository.findScheduleDetailById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        return ScheduleDetailDto.from(scheduleDetail);
    }

    public List<UserScheduleDto> findAllScheduleByUserId(Long userId) {
        List<Schedule> findSchedules = scheduleRepository.findByUserId(userId);
        return findSchedules.stream()
                .map(this::createUserScheduleDto)
                .toList();
    }

    private UserScheduleDto createUserScheduleDto(Schedule schedule) {
        return new UserScheduleDto(schedule.getId(), schedule.getTitle(), schedule.getCreatedDate());
    }

    public Page<SearchScheduleDto> searchSchedule(ScheduleSearchCondition searchCondition) {
        final Page<Schedule> searchSchedule = pageQueryRepository.searchSchedulePage(searchCondition);
        final List<Long> scheduleIds = getScheduleIds(searchSchedule);

        final Map<Long, StatisticsScheduleDetails> statisticsDetailsScheduleIdMap = pageQueryRepository.findStatisticsDetailsScheduleIdMap(scheduleIds);
        final Map<Long, Boolean> likedSchedulesMap = pageQueryRepository.findLikedSchedulesMap(scheduleIds, searchCondition.userId());
        return searchSchedule.map(s -> convertSearchScheduleDto(s, statisticsDetailsScheduleIdMap, likedSchedulesMap));
    }

    private SearchScheduleDto convertSearchScheduleDto(
            Schedule schedule,
            Map<Long, StatisticsScheduleDetails> statisticsScheduleDetailsMap,
            Map<Long, Boolean> likedSchedulesMap
    ) {
        final Long scheduleId = schedule.getId();
        final StatisticsScheduleDetails statisticsScheduleDetails = statisticsScheduleDetailsMap.get(scheduleId);

        return SearchScheduleDto.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .destination(schedule.getDestination())
                .expense(statisticsScheduleDetails.getTotalExpense())
                .userName(schedule.getUser().getName())
                .likesCount(statisticsScheduleDetails.getTotalLikeCount())
                .commentsCount(statisticsScheduleDetails.getTotalCommentCount())
                .viewCount(statisticsScheduleDetails.getTotalViewCount())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .createdDate(LocalDate.from(schedule.getCreatedDate()))
                .isLiked(likedSchedulesMap.get(scheduleId))
                .build();
    }
}
