package com.example.demo.service;

import com.example.demo.enums.Track;
import com.example.demo.model.Graduate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Generates a single XLSX per promotion, with one sheet per track (EL, TN) so both are visible in
 * the same file while staying clearly distinct - as opposed to one file per track.
 *
 * <p>Uses a streaming ({@link SXSSFWorkbook}) workbook rather than the plain in-memory
 * XSSFWorkbook: rows are flushed to disk as they're written instead of being held as objects in the
 * JVM heap, so generating a large promotion's list doesn't scale the server's memory with the
 * student count.
 */
@Component
public class GraduateXlsxGenerator {

  private static final List<String> HEADERS =
      List.of("Matricule", "Nom", "Prénom", "Moyenne générale", "Rang");

  /** Rows kept in memory at a time before being flushed to the temp file on disk. */
  private static final int STREAMING_WINDOW_SIZE = 200;

  /**
   * Expects each track's list already sorted by rank (best first), as returned by
   * GraduationService.
   */
  public File generate(Map<Track, List<Graduate>> graduatesByTrack) {
    var workbook = new SXSSFWorkbook(STREAMING_WINDOW_SIZE);
    try {
      for (Track track : Track.values()) {
        var sheet = workbook.createSheet(track.name());
        writeHeader(sheet);
        writeRows(sheet, graduatesByTrack.getOrDefault(track, List.of()));
      }

      var file = File.createTempFile("graduates-", ".xlsx");
      try (var out = new FileOutputStream(file)) {
        workbook.write(out);
      }
      return file;
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate graduates XLSX", e);
    } finally {
      // Releases the temp files SXSSF uses to back unflushed rows.
      workbook.dispose();
    }
  }

  private void writeHeader(Sheet sheet) {
    var header = sheet.createRow(0);
    for (int i = 0; i < HEADERS.size(); i++) {
      header.createCell(i).setCellValue(HEADERS.get(i));
    }
  }

  private void writeRows(Sheet sheet, List<Graduate> graduates) {
    for (int i = 0; i < graduates.size(); i++) {
      writeRow(sheet.createRow(i + 1), graduates.get(i));
    }
  }

  private void writeRow(Row row, Graduate graduate) {
    var student = graduate.student();

    row.createCell(0).setCellValue(student.reference());
    row.createCell(1).setCellValue(student.lastName());
    row.createCell(2).setCellValue(student.firstName());
    row.createCell(3)
        .setCellValue(
            graduate.overallAverage() == null ? 0 : graduate.overallAverage().doubleValue());
    row.createCell(4).setCellValue(graduate.rank());
  }
}
