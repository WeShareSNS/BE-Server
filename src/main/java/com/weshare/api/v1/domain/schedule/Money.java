package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    @Column(name = "expense")
    private BigDecimal expense;

    public Money(String expense) {
        this.expense = parseExpense(expense);
    }

    private BigDecimal parseExpense(String expense) {
        if (!StringUtils.hasText(expense)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(expense);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("금액 요청이 올바르지 않습니다.");
        }
    }

    private Money(BigDecimal expense) {
        this.expense = expense;
    }

    public Money sum(Money money) {
        return new Money(this.expense.add(money.expense));
    }

    public BigDecimal getValue() {
        return expense;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Money money = (Money) object;
        return Objects.equals(expense, money.expense);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expense);
    }
}
