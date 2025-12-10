package com.saloni.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyTotal {
    private LocalDate date;
    private BigDecimal total;

    public DailyTotal() {}

    public DailyTotal(LocalDate date, BigDecimal total) {
        this.date = date;
        this.total = total;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
