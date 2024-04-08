package com.weshare.api.v1.controller.schedule.query;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ScheduleQueryValidator {
    private static Pattern EXPENSE_PATTERN = Pattern.compile("^\\d*~\\d*$");

    public void validateExpenseCondition(String expenseCondition) {
        if (expenseCondition != null && !EXPENSE_PATTERN.matcher(expenseCondition).matches()) {
            throw new IllegalArgumentException("금액 검색 조건이 올바르지 않습니다.");
        }
    }

}
