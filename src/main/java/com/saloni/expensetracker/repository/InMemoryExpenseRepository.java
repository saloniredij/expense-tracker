package com.saloni.expensetracker.repository;

import com.saloni.expensetracker.model.Expense;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryExpenseRepository implements ExpenseRepository {

    private final List<Expense> expenses = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public synchronized Expense save(Expense expense) {
        if (expense.getId() == 0) {
            expense.setId(idGenerator.getAndIncrement());
        }
        expenses.add(expense);
        return expense;
    }

    @Override
    public synchronized List<Expense> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(expenses));
    }
}
