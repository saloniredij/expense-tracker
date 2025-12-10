package com.saloni.expensetracker;

import com.saloni.expensetracker.model.Expense;
import com.saloni.expensetracker.service.ExpenseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final ExpenseService service;

    public DataLoader(ExpenseService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed some sample expenses for demo/testing
        service.addExpense(new Expense(0, "Food", new BigDecimal("12.50"), LocalDate.now().minusDays(2), "Lunch"));
        service.addExpense(new Expense(0, "Transport", new BigDecimal("3.20"), LocalDate.now().minusDays(1), "Bus"));
        service.addExpense(new Expense(0, "Rent", new BigDecimal("500.00"), LocalDate.now().minusDays(10), "Monthly rent"));
        service.addExpense(new Expense(0, "Food", new BigDecimal("7.80"), LocalDate.now(), "Snack"));
        service.addExpense(new Expense(0, "Utilities", new BigDecimal("60.75"), LocalDate.now().minusDays(5), "Electricity"));
    }
}