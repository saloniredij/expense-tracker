package com.saloni.expensetracker.service;

import com.saloni.expensetracker.model.Expense;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public byte[] generateExcelFile(List<Expense> expenses) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expenses");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Date", "Category", "Amount", "Note"};
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add data rows
            int rowNum = 1;
            for (Expense expense : expenses) {
                Row row = sheet.createRow(rowNum++);
                
                // ID
                row.createCell(0).setCellValue(expense.getId());
                
                // Date (formatted as MM/dd/yyyy)
                row.createCell(1).setCellValue(expense.getDate().format(dateFormatter));
                
                // Category
                row.createCell(2).setCellValue(expense.getCategory() != null ? expense.getCategory() : "");
                
                // Amount
                BigDecimal amount = expense.getAmount() != null ? expense.getAmount() : BigDecimal.ZERO;
                Cell amountCell = row.createCell(3);
                amountCell.setCellValue(amount.doubleValue());
                amountCell.setCellStyle(createCurrencyStyle(workbook));
                
                // Note
                row.createCell(4).setCellValue(expense.getNote() != null ? expense.getNote() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        return style;
    }
}