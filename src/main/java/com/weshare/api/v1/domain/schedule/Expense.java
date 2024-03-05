package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense {

    @Column(name = "expense", nullable = false)
    private long expense;

    public Expense(long expense) {
        this.expense = expense;
    }

    public Expense sum(Expense expense) {
        return new Expense(this.expense + expense.expense);
    }
}
