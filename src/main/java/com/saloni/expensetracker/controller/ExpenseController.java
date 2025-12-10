package com.saloni.expensetracker.controller;

import com.saloni.expensetracker.dto.ExpenseSummary;
import com.saloni.expensetracker.model.Expense;
import com.saloni.expensetracker.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
// @CrossOrigin(origins = "http://localhost:5173")  // for React 
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    // Add a new expense
    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        return service.addExpense(expense);
    }

    // Update an existing expense
    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable long id, @RequestBody Expense expense) {
        try {
            return service.updateExpense(id, expense);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update expense");
        }
    }

    // List all expenses
    @GetMapping
    public List<Expense> getAll() {
        return service.getAllExpenses();
    }

    // Get overall summary (total, per category, trend, highest/lowest)
    @GetMapping("/summary")
    public ExpenseSummary getSummary() {
        return service.getSummary();
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable long id) {
        service.deleteExpense(id);
    }
}