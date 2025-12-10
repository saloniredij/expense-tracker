package com.saloni.expensetracker.controller;

import com.saloni.expensetracker.dto.ExpenseSummary;
import com.saloni.expensetracker.model.Expense;
import com.saloni.expensetracker.service.ExcelExportService;
import com.saloni.expensetracker.service.ExpenseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:5173")  // for React 
public class ExpenseController {

    private final ExpenseService service;
    private final ExcelExportService excelExportService;

    public ExpenseController(ExpenseService service, ExcelExportService excelExportService) {
        this.service = service;
        this.excelExportService = excelExportService;
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

    // Delete an expense
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable long id) {
        service.deleteExpense(id);
    }

    // Export expenses to Excel
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            List<Expense> expenses = service.getAllExpenses();
            byte[] excelFile = excelExportService.generateExcelFile(expenses);

            String fileName = "expenses_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(excelFile);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate Excel file", ex);
        }
    }
}