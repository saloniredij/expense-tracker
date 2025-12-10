package com.saloni.expensetracker.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saloni.expensetracker.model.Expense;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@Repository
public class FileExpenseRepository implements ExpenseRepository {

    private final Path filePath;
    private final List<Expense> expenses = new ArrayList<>();
    private long nextId = 1;

    private final ObjectMapper objectMapper;

    public FileExpenseRepository(ObjectMapper objectMapper, 
                               @Value("${expense.file.path:expenses.json}") String expenseFilePath) {
        this.objectMapper = objectMapper;
        this.filePath = Paths.get(expenseFilePath);
        loadFromFile();
    }

    private synchronized void loadFromFile() {
        if (Files.exists(filePath)) {
            try (Reader reader = Files.newBufferedReader(filePath)) {
                Expense[] arr = objectMapper.readValue(reader, Expense[].class);
                expenses.clear();
                if (arr != null) {
                    expenses.addAll(Arrays.asList(arr));
                }
                nextId = expenses.stream()
                        .mapToLong(Expense::getId)
                        .max()
                        .orElse(0L) + 1;
            } catch (IOException e) {
                throw new RuntimeException("Failed to load expenses from file", e);
            }
        } else {
            // OPTIONAL: preload demo data into the file the first time
            expenses.add(new Expense(0, "Food",
                    new BigDecimal("12.50"), LocalDate.now().minusDays(2), "Lunch"));
            expenses.add(new Expense(0, "Travel",
                    new BigDecimal("3.20"), LocalDate.now().minusDays(1), "Bus"));
            expenses.add(new Expense(0, "Utilities",
                    new BigDecimal("500.00"), LocalDate.now().minusDays(10), "Monthly rent"));

            for (Expense e : expenses) {
                e.setId(nextId++);
            }
            saveToFile();
        }
    }

    private synchronized void saveToFile() {
        try (Writer writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValue(writer, expenses);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save expenses to file", e);
        }
    }

    @Override
    public synchronized Expense save(Expense expense) {
        if (expense.getId() == 0) {
            // new expense
            expense.setId(nextId++);
            expenses.add(expense);
        } else {
            // update existing if needed (not strictly used yet)
            boolean updated = false;
            for (int i = 0; i < expenses.size(); i++) {
                if (expenses.get(i).getId() == expense.getId()) {
                    expenses.set(i, expense);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                expenses.add(expense);
            }
        }

        saveToFile();
        return expense;
    }

    @Override
    public synchronized List<Expense> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(expenses));
    }

    @Override
    public synchronized void deleteById(long id) {
        expenses.removeIf(e -> e.getId() == id);
        saveToFile();
    }
}