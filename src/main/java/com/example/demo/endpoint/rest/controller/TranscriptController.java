package com.example.demo.endpoint.rest.controller;

import com.example.demo.model.transcript.FullTranscript;
import com.example.demo.model.transcript.YearTranscript;
import com.example.demo.service.TranscriptService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transcripts")
@AllArgsConstructor
public class TranscriptController {

  private final TranscriptService transcriptService;

  @GetMapping("/year")
  public YearTranscript year(@RequestParam UUID studentId, @RequestParam UUID academicYearId) {
    return transcriptService.yearTranscript(studentId, academicYearId);
  }

  @GetMapping("/full")
  public FullTranscript full(@RequestParam UUID studentId) {
    return transcriptService.fullTranscript(studentId);
  }
}
