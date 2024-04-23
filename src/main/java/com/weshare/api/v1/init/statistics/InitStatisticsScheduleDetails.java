package com.weshare.api.v1.init.statistics;

import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.ScheduleIdProvider;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.like.ScheduleLikeRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.repository.schedule.statistics.StatisticsScheduleDetailsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Profile("init")
@Component
@RequiredArgsConstructor
public class InitStatisticsScheduleDetails {
    private final InitStatisticsScheduleDetailsService initStatisticsScheduleDetailsService;

    @PostConstruct
    public void init() {
        initStatisticsScheduleDetailsService.initScheduleDetailsService();
    }

    @Component
    @RequiredArgsConstructor
    static class InitStatisticsScheduleDetailsService {
        private final ScheduleRepository scheduleRepository;
        private final ScheduleLikeRepository scheduleLikeRepository;
        private final CommentRepository commentRepository;
        private final StatisticsScheduleDetailsRepository scheduleDetailsRepository;

        // 데이터 많아질 수록 로딩이 엄청 오래걸림(우선 pagination으로 가져오기)
        @Transactional
        public void initScheduleDetailsService() {
            final List<Schedule> schedules = scheduleRepository.findAll();
            final List<Long> scheduleIds = getScheduleIds(schedules);
            final Map<Long, Schedule> scheduleMap = getScheduleMap(scheduleIds, schedules);
            // Map으로 바꾸기
            final Map<Long, Long> likeCountMap = convertCountMap(scheduleLikeRepository.findLikeByScheduleIds(scheduleIds));
            final Map<Long, Long> commentCountMap = convertCountMap(commentRepository.findCommentByScheduleIds(scheduleIds));

            for (Map.Entry<Long, Schedule> scheduleEntry : scheduleMap.entrySet()) {
                Long scheduleId = scheduleEntry.getKey();
                Schedule schedule = scheduleEntry.getValue();

                StatisticsScheduleDetails statisticsScheduleDetails = StatisticsScheduleDetails.builder()
                        .scheduleId(scheduleId)
                        .totalExpense(schedule.getTotalScheduleExpense())
                        .totalViewCount(schedule.getViewCount())
                        // 여행 일정이 생성될 때 좋아요, 댓글이 생기기 않기 때문에 null일 수 있다.
                        .totalLikeCount(getCountOrElseZero(likeCountMap.get(scheduleId)))
                        .totalCommentCount(getCountOrElseZero(commentCountMap.get(scheduleId)))
                        .build();

                scheduleDetailsRepository.save(statisticsScheduleDetails);
            }
        }

        private List<Long> getScheduleIds(List<Schedule> schedules) {
            return schedules.stream()
                    .map(Schedule::getId)
                    .toList();
        }

        private Map<Long, Schedule> getScheduleMap(List<Long> scheduleIds, List<Schedule> schedules) {
            final Map<Long, Schedule> scheduleMap = scheduleIds.stream()
                    .collect(toMap(
                            Function.identity(),
                            id -> schedules.stream()
                                    .filter(s -> s.getId().equals(id))
                                    .findFirst()
                                    .orElseThrow()
                    ));
            return Collections.unmodifiableMap(scheduleMap);
        }


        private int getCountOrElseZero(Long count) {
            return Optional.ofNullable(count)
                    .orElse(0L)
                    .intValue();
        }

        private Map<Long, Long> convertCountMap(List<? extends ScheduleIdProvider> commentOrLike) {
            return commentOrLike.stream()
                    .collect(groupingBy(
                            ScheduleIdProvider::getScheduleId,
                            Collectors.counting()
                    ));
        }

    }
}
