package com.saloni.expensetracker.repository;

import com.saloni.expensetracker.model.Expense;

import java.util.List;

public interface ExpenseRepository {

    Expense save(Expense expense);
    List<Expense> findAll();
    void deleteById(long id);
}
