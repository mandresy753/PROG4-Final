package com.example.demo.service;

import com.example.demo.model.transcript.FullTranscript;
import com.example.demo.model.transcript.TranscriptCourseLine;
import com.example.demo.model.transcript.YearTranscript;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

@Component
public class TranscriptPdfGenerator {

    private static final float MARGIN = 50f;
    private static final float LINE_HEIGHT = 16f;

    public File generate(FullTranscript transcript) {
        try (var document = new PDDocument()) {
            var writer = new PdfCursor(document);

            writer.title("Relevé de notes");
            writer.line(transcript.student().firstName() + " " + transcript.student().lastName());
            writer.line("Statut : " + transcript.status());
            writer.line("Moyenne générale : " + formatAverage(transcript.overallAverage()));
            writer.line("Crédits totaux : " + transcript.totalCredits());
            writer.blank();

            for (var year : transcript.years()) {
                writeYear(writer, year);
            }

            var file = File.createTempFile("transcript-", ".pdf");
            document.save(file);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate transcript PDF", e);
        }
    }

    private void writeYear(PdfCursor writer, YearTranscript year) throws IOException {
        writer.subtitle("Année " + year.academicYear().label() + " - " + year.status());

        for (var course : year.courses()) {
            writer.line(formatCourseLine(course));
        }

        writer.line(
                "Moyenne annuelle : "
                        + formatAverage(year.generalAverage())
                        + " - Crédits validés : "
                        + year.validatedCredits()
                        + "/"
                        + year.totalCredits());
        writer.blank();
    }

    private String formatCourseLine(TranscriptCourseLine course) {
        return "%s - %s : %s (%d crédits, %s)"
                .formatted(
                        course.courseRef(),
                        course.courseTitle(),
                        formatAverage(course.average()),
                        course.creditCount(),
                        course.status());
    }

    private String formatAverage(BigDecimal average) {
        return average == null ? "N/A" : average.toString();
    }

    private static class PdfCursor implements AutoCloseable {
        private final PDDocument document;
        private PDPageContentStream contentStream;
        private float cursorY;

        PdfCursor(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        void title(String text) throws IOException {
            write(text, PDType1Font.HELVETICA_BOLD, 16);
        }

        void subtitle(String text) throws IOException {
            write(text, PDType1Font.HELVETICA_BOLD, 12);
        }

        void line(String text) throws IOException {
            write(text, PDType1Font.HELVETICA, 10);
        }

        void blank() throws IOException {
            cursorY -= LINE_HEIGHT / 2;
        }

        private void write(String text, PDType1Font font, int fontSize) throws IOException {
            if (cursorY < MARGIN) {
                newPage();
            }
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(MARGIN, cursorY);
            contentStream.showText(text);
            contentStream.endText();
            cursorY -= LINE_HEIGHT;
        }

        private void newPage() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }
            var page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            cursorY = PDRectangle.A4.getHeight() - MARGIN;
        }

        @Override
        public void close() throws IOException {
            contentStream.close();
        }
    }
}
