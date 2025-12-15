package com.saloni.expensetracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.saloni.expensetracker.dto.CategoryTotal;
import com.saloni.expensetracker.dto.DailyTotal;
import com.saloni.expensetracker.dto.ExpenseSummary;
import com.saloni.expensetracker.model.Expense;
import com.saloni.expensetracker.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense addExpense(Expense expense) {
        // default date = current
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        return repository.save(expense);
    }

    public Expense updateExpense(long id, Expense expense) {
        // set id to ensure update
        expense.setId(id);

        // validation: date not in the future
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        if (expense.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Expense date cannot be in the future");
        }

        // validation: amount must be > 0
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        // trim note length (ensure not longer than 20)
        if (expense.getNote() != null && expense.getNote().length() > 20) {
            expense.setNote(expense.getNote().substring(0, 20));
        }

        return repository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    public BigDecimal getTotalExpense() {
        return repository.findAll().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> getTotalByCategory() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().toLowerCase(Locale.ROOT),
                        Collectors.mapping(
                                Expense::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    public List<DailyTotal> getDailyTrend() {
        Map<LocalDate, BigDecimal> byDate = repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Expense::getDate,
                        Collectors.mapping(
                                Expense::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        return byDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new DailyTotal(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public CategoryTotal getHighestCategory() {
        Map<String, BigDecimal> totals = getTotalByCategory();
        return totals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new CategoryTotal(e.getKey(), e.getValue()))
                .orElse(null);
    }

    public CategoryTotal getLowestCategory() {
        Map<String, BigDecimal> totals = getTotalByCategory();
        return totals.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(e -> new CategoryTotal(e.getKey(), e.getValue()))
                .orElse(null);
    }

    public ExpenseSummary getSummary() {
        BigDecimal total = getTotalExpense();
        Map<String, BigDecimal> byCategory = getTotalByCategory();
        List<DailyTotal> trend = getDailyTrend();
        CategoryTotal highest = getHighestCategory();
        CategoryTotal lowest = getLowestCategory();
        return new ExpenseSummary(total, byCategory, trend, highest, lowest);
    }

    public void deleteExpense(long id) {
        repository.deleteById(id);
    }
}