package com.example.demo.service;

import com.example.demo.enums.Track;
import com.example.demo.model.Graduate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class GraduateXlsxGenerator {

  private static final List<Track> GRADUATION_TRACKS = List.of(Track.EL, Track.TN);

  private static final List<String> HEADERS =
      List.of("Matricule", "Nom", "Prénom", "Moyenne générale", "Rang");

  private static final int STREAMING_WINDOW_SIZE = 200;

  public File generate(Map<Track, List<Graduate>> graduatesByTrack) {
    var workbook = new SXSSFWorkbook(STREAMING_WINDOW_SIZE);
    try {
      var headerStyle = createHeaderStyle(workbook);
      var averageStyle = createAverageStyle(workbook);

      for (Track track : GRADUATION_TRACKS) {
        var sheet = workbook.createSheet(track.name());
        writeHeader(sheet, headerStyle);
        writeRows(sheet, graduatesByTrack.getOrDefault(track, List.of()), averageStyle);
        applyColumnWidths(sheet);
      }

      var file = File.createTempFile("graduates-", ".xlsx");
      try (var out = new FileOutputStream(file)) {
        workbook.write(out);
      }
      return file;
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate graduates XLSX", e);
    } finally {

      workbook.dispose();
    }
  }

  private CellStyle createHeaderStyle(SXSSFWorkbook workbook) {
    var font = workbook.createFont();
    font.setBold(true);
    var style = workbook.createCellStyle();
    style.setFont(font);
    return style;
  }

  private CellStyle createAverageStyle(SXSSFWorkbook workbook) {
    var style = workbook.createCellStyle();
    style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
    return style;
  }

  private void writeHeader(Sheet sheet, CellStyle headerStyle) {
    var header = sheet.createRow(0);
    for (int i = 0; i < HEADERS.size(); i++) {
      var cell = header.createCell(i);
      cell.setCellValue(HEADERS.get(i));
      cell.setCellStyle(headerStyle);
    }
    sheet.createFreezePane(0, 1);
  }

  private void writeRows(Sheet sheet, List<Graduate> graduates, CellStyle averageStyle) {
    for (int i = 0; i < graduates.size(); i++) {
      writeRow(sheet.createRow(i + 1), graduates.get(i), averageStyle);
    }
  }

  private void writeRow(Row row, Graduate graduate, CellStyle averageStyle) {
    var student = graduate.student();

    row.createCell(0).setCellValue(student.reference());
    row.createCell(1).setCellValue(student.lastName());
    row.createCell(2).setCellValue(student.firstName());

    var averageCell = row.createCell(3);
    averageCell.setCellValue(
        graduate.overallAverage() == null ? 0 : graduate.overallAverage().doubleValue());
    averageCell.setCellStyle(averageStyle);

    row.createCell(4).setCellValue(graduate.rank());
  }

  private void applyColumnWidths(Sheet sheet) {
    sheet.setColumnWidth(0, 4000);
    sheet.setColumnWidth(1, 6000);
    sheet.setColumnWidth(2, 6000);
    sheet.setColumnWidth(3, 5000);
    sheet.setColumnWidth(4, 3000);
  }
}
