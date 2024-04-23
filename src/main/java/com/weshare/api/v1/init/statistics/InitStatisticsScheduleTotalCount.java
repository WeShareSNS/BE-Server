package com.weshare.api.v1.init.statistics;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleTotalCount;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.repository.schedule.statistics.StatisticsScheduleTotalCountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitStatisticsScheduleTotalCount {
    private final InitStatisticsScheduleTotalCountService initStatisticsScheduleTotalCountService;

    @PostConstruct
    public void init() {
        initStatisticsScheduleTotalCountService.initTotalCount();
    }

    @Component
    @RequiredArgsConstructor
    static class InitStatisticsScheduleTotalCountService {

        private final StatisticsScheduleTotalCountRepository scheduleTotalCountRepository;
        private final ScheduleRepository scheduleRepository;

        @Transactional
        public void initTotalCount() {
            final long count = scheduleRepository.count();
            final StatisticsScheduleTotalCount statisticsScheduleTotalCount = scheduleTotalCountRepository.findFirstByOrderByModifiedDate()
                    .orElseGet(this::saveScheduleTotalCount);
            statisticsScheduleTotalCount.syncScheduleTotalCount(count);
        }

        private StatisticsScheduleTotalCount saveScheduleTotalCount() {
            return scheduleTotalCountRepository.save(new StatisticsScheduleTotalCount());
        }
    }
}
