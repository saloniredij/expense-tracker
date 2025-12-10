package com.saloni.expensetracker.service;

import com.saloni.expensetracker.model.Expense;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelExportServiceTest {

    private ExcelExportService service;

    @BeforeEach
    void setUp() {
        service = new ExcelExportService();
    }

    @Test
    void testGenerateExcelFileWithValidExpenses() throws Exception {
        Expense e1 = new Expense(1, "Food", new BigDecimal("12.50"), LocalDate.of(2025, 12, 11), "Lunch");
        Expense e2 = new Expense(2, "Travel", new BigDecimal("3.20"), LocalDate.of(2025, 12, 10), "Bus");
        List<Expense> expenses = Arrays.asList(e1, e2);

        byte[] excelBytes = service.generateExcelFile(expenses);
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheet("Expenses");
            assertNotNull(sheet);

            Row header = sheet.getRow(0);
            assertEquals("ID", header.getCell(0).getStringCellValue());
            assertEquals("Date", header.getCell(1).getStringCellValue());
            assertEquals("Category", header.getCell(2).getStringCellValue());
            assertEquals("Amount", header.getCell(3).getStringCellValue());
            assertEquals("Note", header.getCell(4).getStringCellValue());

            Row row1 = sheet.getRow(1);
            assertEquals(1, (int) row1.getCell(0).getNumericCellValue());
            assertEquals("12/11/2025", row1.getCell(1).getStringCellValue());
            assertEquals("Food", row1.getCell(2).getStringCellValue());
            assertEquals(12.50, row1.getCell(3).getNumericCellValue());
            assertEquals("Lunch", row1.getCell(4).getStringCellValue());

            Row row2 = sheet.getRow(2);
            assertEquals(2, (int) row2.getCell(0).getNumericCellValue());
            assertEquals("12/10/2025", row2.getCell(1).getStringCellValue());
            assertEquals("Travel", row2.getCell(2).getStringCellValue());
            assertEquals(3.20, row2.getCell(3).getNumericCellValue());
            assertEquals("Bus", row2.getCell(4).getStringCellValue());
        }
    }
}
