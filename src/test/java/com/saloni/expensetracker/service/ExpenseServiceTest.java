package com.saloni.expensetracker.service;

import com.saloni.expensetracker.dto.CategoryTotal;
import com.saloni.expensetracker.dto.DailyTotal;
import com.saloni.expensetracker.dto.ExpenseSummary;
import com.saloni.expensetracker.model.Expense;
import com.saloni.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    private ExpenseRepository repository;
    private ExpenseService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExpenseRepository.class);
        service = new ExpenseService(repository);
    }

    @Test
    void testAddExpenseSetsDateIfNull() {
        Expense expense = new Expense(0, "Food", new BigDecimal("10.0"), null, "Lunch");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Expense saved = service.addExpense(expense);

        assertNotNull(saved.getDate());
        verify(repository, times(1)).save(expense);
    }

    @Test
    void testUpdateExpenseValidations() {
        Expense expense = new Expense(0, "Travel", new BigDecimal("20"), LocalDate.now().plusDays(1), "Train");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.updateExpense(1, expense));
        assertEquals("Expense date cannot be in the future", ex.getMessage());

        expense.setDate(LocalDate.now());
        expense.setAmount(BigDecimal.ZERO);

        ex = assertThrows(IllegalArgumentException.class, () -> service.updateExpense(1, expense));
        assertEquals("Amount must be greater than 0", ex.getMessage());
    }

    @Test
    void testUpdateExpenseTrimsNote() {
        String longNote = "This note is definitely longer than twenty characters";
        Expense expense = new Expense(0, "Food", new BigDecimal("15.0"), LocalDate.now(), longNote);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Expense updated = service.updateExpense(1, expense);
        assertTrue(updated.getNote().length() <= 20);
    }

    @Test
    void testGetAllExpenses() {
        Expense e1 = new Expense(1, "Food", new BigDecimal("10"), LocalDate.now(), "Lunch");
        Expense e2 = new Expense(2, "Travel", new BigDecimal("5"), LocalDate.now(), "Bus");
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<Expense> all = service.getAllExpenses();
        assertEquals(2, all.size());
    }

    @Test
    void testGetTotalExpense() {
        Expense e1 = new Expense(1, "Food", new BigDecimal("10"), LocalDate.now(), "Lunch");
        Expense e2 = new Expense(2, "Travel", new BigDecimal("5"), LocalDate.now(), "Bus");
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2));

        BigDecimal total = service.getTotalExpense();
        assertEquals(new BigDecimal("15"), total);
    }

    @Test
    void testGetTotalByCategory() {
        Expense e1 = new Expense(1, "Food", new BigDecimal("10"), LocalDate.now(), "Lunch");
        Expense e2 = new Expense(2, "food", new BigDecimal("5"), LocalDate.now(), "Snack");
        Expense e3 = new Expense(3, "Travel", new BigDecimal("7"), LocalDate.now(), "Bus");
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2, e3));

        Map<String, BigDecimal> totals = service.getTotalByCategory();
        assertEquals(new BigDecimal("15"), totals.get("food"));
        assertEquals(new BigDecimal("7"), totals.get("travel"));
    }

    @Test
    void testGetDailyTrendSorted() {
        Expense e1 = new Expense(1, "Food", new BigDecimal("10"), LocalDate.now().minusDays(1), "Lunch");
        Expense e2 = new Expense(2, "Travel", new BigDecimal("5"), LocalDate.now(), "Bus");
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<DailyTotal> trend = service.getDailyTrend();
        assertEquals(2, trend.size());
        assertTrue(trend.get(0).getDate().isBefore(trend.get(1).getDate()) ||
                   trend.get(0).getDate().isEqual(trend.get(1).getDate()));
    }

    @Test
    void testGetHighestAndLowestCategory() {
        Expense e1 = new Expense(1, "Food", new BigDecimal("10"), LocalDate.now(), "Lunch");
        Expense e2 = new Expense(2, "Travel", new BigDecimal("20"), LocalDate.now(), "Bus");
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2));

        CategoryTotal highest = service.getHighestCategory();
        CategoryTotal lowest = service.getLowestCategory();

        assertEquals("travel", highest.getCategory().toLowerCase());
        assertEquals(new BigDecimal("20"), highest.getTotal());
        assertEquals("food", lowest.getCategory().toLowerCase());
        assertEquals(new BigDecimal("10"), lowest.getTotal());
    }

    @Test
    void testGetSummary() {
        Expense e1 = new Expense(1, "Food", new BigDecimal("10"), LocalDate.now(), "Lunch");
        Expense e2 = new Expense(2, "Travel", new BigDecimal("20"), LocalDate.now(), "Bus");
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2));

        ExpenseSummary summary = service.getSummary();
        assertEquals(new BigDecimal("30"), summary.getTotalExpense());
        assertEquals(2, summary.getTotalByCategory().size());
    }

    @Test
    void testDeleteExpense() {
        doNothing().when(repository).deleteById(1L);
        service.deleteExpense(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
