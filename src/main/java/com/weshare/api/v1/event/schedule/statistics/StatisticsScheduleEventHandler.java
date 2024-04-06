package com.weshare.api.v1.event.schedule.statistics;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleTotalCount;
import com.weshare.api.v1.event.schedule.ScheduleCreatedEvent;
import com.weshare.api.v1.repository.schedule.statistics.StatisticsScheduleDetailsRepository;
import com.weshare.api.v1.repository.schedule.statistics.StatisticsScheduleTotalCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StatisticsScheduleEventHandler {

    private final StatisticsScheduleDetailsRepository scheduleDetailsRepository;
    private final StatisticsScheduleTotalCountRepository scheduleTotalCountRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void scheduleCreatedEvent(ScheduleCreatedEvent createdEvent) {
        StatisticsScheduleDetails statisticsScheduleDetails = createStatisticsScheduleDetails(createdEvent);
        scheduleDetailsRepository.save(statisticsScheduleDetails);

        StatisticsScheduleTotalCount statisticsScheduleTotalCount = scheduleTotalCountRepository.findFirstByOrderByModifiedDate()
                .orElseGet(this::saveScheduleTotalCount);
        statisticsScheduleTotalCount.incrementTotalCount();
    }

    private StatisticsScheduleDetails createStatisticsScheduleDetails(ScheduleCreatedEvent createdEvent) {
        return StatisticsScheduleDetails.builder()
                .scheduleId(createdEvent.scheduleId())
                .totalExpense(createdEvent.totalExpense())
                .build();
    }

    private StatisticsScheduleTotalCount saveScheduleTotalCount() {
        return scheduleTotalCountRepository.save(new StatisticsScheduleTotalCount());
    }
}