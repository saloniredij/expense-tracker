package com.saloni.expensetracker.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.saloni.expensetracker.model.Expense;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileExpenseRepositoryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private FileExpenseRepository repository;
    private Path tempFile;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = Files.createTempFile("expenses-test", ".json");
        Files.delete(tempFile);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        repository = new FileExpenseRepository(objectMapper, tempFile.toString());
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testSaveAndFindAll() {
        Expense expense = new Expense(0, "Coffee", new BigDecimal("2.50"), LocalDate.now(), "Morning coffee");
        repository.save(expense);

        List<Expense> allExpenses = repository.findAll();
        assertFalse(allExpenses.isEmpty());
        assertTrue(allExpenses.stream().anyMatch(e -> "Coffee".equals(e.getCategory())));
    }


    @Test
    void testUpdateExpense() {
        Expense expense = new Expense(0, "Lunch", new BigDecimal("10.00"), LocalDate.now(), "Salad");
        Expense saved = repository.save(expense);

        saved.setAmount(new BigDecimal("12.00"));
        repository.save(saved);

        Expense updated = repository.findAll().stream()
                .filter(e -> e.getId() == saved.getId())
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("12.00"), updated.getAmount());
    }

    @Test
    void testDeleteExpense() {
        Expense expense = new Expense(0, "Snack", new BigDecimal("1.50"), LocalDate.now(), "Chips");
        Expense saved = repository.save(expense);

        repository.deleteById(saved.getId());
        assertTrue(repository.findAll().stream().noneMatch(e -> e.getId() == saved.getId()));
    }

    @Test
    void testInitializeDemoData() {
        FileExpenseRepository newRepo = new FileExpenseRepository(objectMapper, tempFile.toString());
        assertFalse(newRepo.findAll().isEmpty());
    }
}
