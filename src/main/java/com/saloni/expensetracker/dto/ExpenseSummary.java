package com.saloni.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ExpenseSummary {

    private BigDecimal totalExpense;
    private Map<String, BigDecimal> totalByCategory;
    private List<DailyTotal> dailyTrend;
    private CategoryTotal highestCategory;
    private CategoryTotal lowestCategory;

    public ExpenseSummary() {}

    public ExpenseSummary(BigDecimal totalExpense,
                          Map<String, BigDecimal> totalByCategory,
                          List<DailyTotal> dailyTrend,
                          CategoryTotal highestCategory,
                          CategoryTotal lowestCategory) {
        this.totalExpense = totalExpense;
        this.totalByCategory = totalByCategory;
        this.dailyTrend = dailyTrend;
        this.highestCategory = highestCategory;
        this.lowestCategory = lowestCategory;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Map<String, BigDecimal> getTotalByCategory() {
        return totalByCategory;
    }

    public void setTotalByCategory(Map<String, BigDecimal> totalByCategory) {
        this.totalByCategory = totalByCategory;
    }

    public List<DailyTotal> getDailyTrend() {
        return dailyTrend;
    }

    public void setDailyTrend(List<DailyTotal> dailyTrend) {
        this.dailyTrend = dailyTrend;
    }

    public CategoryTotal getHighestCategory() {
        return highestCategory;
    }

    public void setHighestCategory(CategoryTotal highestCategory) {
        this.highestCategory = highestCategory;
    }

    public CategoryTotal getLowestCategory() {
        return lowestCategory;
    }

    public void setLowestCategory(CategoryTotal lowestCategory) {
        this.lowestCategory = lowestCategory;
    }
}
