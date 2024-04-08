package com.weshare.api.v1.service.schedule.query.dto;

import com.weshare.api.v1.repository.schedule.query.ExpenseCondition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseConditionTest {

    @ParameterizedTest
    @CsvSource(value = {
            "~123, 0, 123",
            "123~, 123, 0",
            "123~123, 123, 123",
            "~, 0, 0"})
    public void 금액_범위로_지정된_문자를_변환할_수_있다(String condition, long min, long max) {
        // when
        ExpenseCondition expenseCondition = ExpenseCondition.convert(condition);
        // then
        Long minExpense = min == 0 ? null : min;
        Long maxExpense = max == 0 ? null : max;
        assertThat(expenseCondition.minExpense()).isEqualTo(minExpense);
        assertThat(expenseCondition.maxExpense()).isEqualTo(maxExpense);
    }

}