package com.weshare.api.v1.controller.schedule.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ScheduleQueryValidatorTest {
    private final ScheduleQueryValidator queryValidator = new ScheduleQueryValidator();

    @ParameterizedTest
    @ValueSource(strings = {"~123", "123~", "123~123", "~"})
    public void 금액_범위로_지정된_문자를_변환할_수_있다(String condition) {
        // when // then
        Assertions.assertDoesNotThrow(() -> queryValidator.validateExpenseCondition(condition));
    }

}