package com.example.demo.service;

import com.example.demo.model.Graduate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class GraduateXlsxGenerator {

    private static final List<String> HEADERS =
            List.of("Nom", "Prénom", "Email", "Parcours", "Moyenne générale", "Crédits totaux");

    public File generate(List<Graduate> graduates) {
        try (var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Diplômés");
            writeHeader(sheet);
            writeRows(sheet, graduates);

            var file = File.createTempFile("graduates-", ".xlsx");
            try (var out = new FileOutputStream(file)) {
                workbook.write(out);
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate graduates XLSX", e);
        }
    }

    private void writeHeader(XSSFSheet sheet) {
        var header = sheet.createRow(0);
        for (int i = 0; i < HEADERS.size(); i++) {
            header.createCell(i).setCellValue(HEADERS.get(i));
        }
    }

    private void writeRows(XSSFSheet sheet, List<Graduate> graduates) {
        for (int i = 0; i < graduates.size(); i++) {
            writeRow(sheet.createRow(i + 1), graduates.get(i));
        }
    }

    private void writeRow(Row row, Graduate graduate) {
        var student = graduate.student();

        row.createCell(0).setCellValue(student.lastName());
        row.createCell(1).setCellValue(student.firstName());
        row.createCell(2).setCellValue(student.email());
        row.createCell(3).setCellValue(graduate.track().name());
        row.createCell(4)
                .setCellValue(
                        graduate.overallAverage() == null ? 0 : graduate.overallAverage().doubleValue());
        row.createCell(5).setCellValue(graduate.totalCredits());
    }
}
