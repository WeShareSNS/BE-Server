package com.weshare.api.v1.repository.schedule;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.weshare.api.v1.utils.QueryDslUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.weshare.api.v1.domain.schedule.QSchedule.schedule;

@Component
public class ScheduleOrderSpecifierHelper {

    public List<OrderSpecifier> getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort()
                .stream()
                .map(this::getOrderSpecifier)
                .toList();
    }

    private OrderSpecifier getOrderSpecifier(Sort.Order order) {
        Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
        switch (order.getProperty()) {
            case "title" -> {
                return QueryDslUtil.getSortedColumn(direction, schedule, "title");
            }
            case "destination" -> {
                return QueryDslUtil.getSortedColumn(direction, schedule, "destination");
            }
            case "createdDate" -> {
                return QueryDslUtil.getSortedColumn(direction, schedule, "createdDate");
            }
            default -> throw new IllegalStateException("정렬 조건이 올바르지 않습니다.");
        }
    }
}