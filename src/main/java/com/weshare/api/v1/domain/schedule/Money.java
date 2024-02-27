package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    @Column(name = "expense")
    private BigDecimal expense;

    public Money(long expense) {
        this.expense = new BigDecimal(expense);
    }

    public Money(BigDecimal expense) {
        this.expense = expense;
    }

    public long sum(Money money) {
        return this.expense.add(money.expense).longValue();
    }

    public long getValue() {
        return expense.longValue();
    }
}
