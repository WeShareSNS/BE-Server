package com.weshare.api.v1.repository.schedule.query;

import org.springframework.util.StringUtils;

public record ExpenseCondition(
        Long minExpense,
        Long maxExpense
) {
    public static ExpenseCondition convert(String condition) {
        if (!StringUtils.hasText(condition)) {
            return new ExpenseCondition(null, null);
        }
        Long minPrice = null;
        StringBuilder sb = new StringBuilder(condition.length());

        for (char ch : condition.toCharArray()) {
            if (ch == '~') {
                minPrice = sb.isEmpty() ? null : Long.parseLong(sb.toString());
                sb.setLength(0);
                continue;
            }
            sb.append(ch);
        }
        Long maxPrice = sb.isEmpty() ? null : Long.parseLong(sb.toString());
        return new ExpenseCondition(minPrice, maxPrice);
    }

    public boolean isNotCondition() {
        return minExpense == null && maxExpense == null;
    }
}
