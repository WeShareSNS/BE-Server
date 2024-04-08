package com.weshare.api.v1.repository.schedule.query.dto;

import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.repository.schedule.query.ExpenseCondition;
import com.weshare.api.v1.repository.schedule.query.SearchCondition;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

@Getter
public class ScheduleConditionPageDto {
    private final Long userId;
    private final List<Destination> destinations;
    private final ExpenseCondition expenseCondition;
    private final SearchCondition searchCondition;
    private final Pageable pageable;

    @Builder
    private ScheduleConditionPageDto(
            Long userId,
            List<Destination> destinations,
            ExpenseCondition expenseCondition,
            SearchCondition searchCondition,
            Pageable pageable
    ) {
        this.userId = userId;
        this.destinations = destinations;
        this.expenseCondition = expenseCondition;
        this.searchCondition = searchCondition;
        this.pageable = pageable;
    }

    public List<Destination> getDestinations() {
        return Collections.unmodifiableList(destinations);
    }
}
