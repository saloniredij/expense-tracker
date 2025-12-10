package com.saloni.expensetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saloni.expensetracker.dto.ExpenseSummary;
import com.saloni.expensetracker.model.Expense;
import com.saloni.expensetracker.service.ExcelExportService;
import com.saloni.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private ExcelExportService excelExportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateExpense() throws Exception {
        Expense expense = new Expense(1L, "Food", BigDecimal.valueOf(200), LocalDate.now(), "note");
        when(expenseService.addExpense(any(Expense.class))).thenReturn(expense);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("Food"));
    }



    @Test
    void testUpdateExpense() throws Exception {
        Expense updated = new Expense(1L, "Travel", BigDecimal.valueOf(400), LocalDate.now(), "note");
        when(expenseService.updateExpense(eq(1L), any(Expense.class))).thenReturn(updated);

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Travel"));
    }

    @Test
    void testGetAllExpenses() throws Exception {
        List<Expense> expenses = Arrays.asList(
                new Expense(3L, "Food", BigDecimal.valueOf(200), LocalDate.now(), "note"),
                new Expense(4L, "Other", BigDecimal.valueOf(500), LocalDate.now(), "note")
        );
        when(expenseService.getAllExpenses()).thenReturn(expenses);

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetSummary() throws Exception {
        ExpenseSummary summary = new ExpenseSummary(BigDecimal.valueOf(700.0), null, null, null, null);
        when(expenseService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/expenses/summary"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteExpense() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).deleteExpense(1L);
    }

    @Test
    void testExportToExcel() throws Exception {
        byte[] dummyFile = "excel-content".getBytes();
        when(expenseService.getAllExpenses())
                .thenReturn(List.of(new Expense(5L, "Food", BigDecimal.valueOf(200), LocalDate.now(), "note")));
        when(excelExportService.generateExcelFile(anyList())).thenReturn(dummyFile);

        mockMvc.perform(get("/api/expenses/export/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("expenses_" + LocalDate.now())))
                .andExpect(content().bytes(dummyFile));
    }
}
