package com.example.demo.service;

import com.example.demo.entity.JExam;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ExamMapper;
import com.example.demo.model.Exam;
import com.example.demo.repository.CourseOfferingRepository;
import com.example.demo.repository.ExamRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExamService {

  private final ExamRepository examRepository;
  private final CourseOfferingRepository courseOfferingRepository;
  private final ExamMapper examMapper;

  public List<Exam> findByCourseOffering(UUID courseOfferingId) {
    return examRepository.findByCourseOffering_Id(courseOfferingId).stream()
        .map(examMapper::toModel)
        .toList();
  }

  public Exam findById(UUID id) {
    return examRepository
        .findById(id)
        .map(examMapper::toModel)
        .orElseThrow(() -> ResourceNotFoundException.of("Exam", id));
  }

  public Exam create(UUID courseOfferingId, LocalDateTime examDate, BigDecimal coefficient) {
    if (!courseOfferingRepository.existsById(courseOfferingId)) {
      throw ResourceNotFoundException.of("Course offering", courseOfferingId);
    }

    if (coefficient.compareTo(BigDecimal.ZERO) <= 0 || coefficient.compareTo(BigDecimal.ONE) > 0) {
      throw new BadRequestException(
          "The exam coefficient must be strictly greater than 0 and at most 1");
    }

    var sumExisting =
        examRepository.findByCourseOffering_Id(courseOfferingId).stream()
            .map(JExam::getCoefficient)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (sumExisting.add(coefficient).compareTo(BigDecimal.ONE) > 0) {
      throw new BadRequestException(
          "The sum of exam coefficients for this course offering would exceed 1 (current: "
              + sumExisting
              + ")");
    }

    var entity =
        JExam.builder()
            .courseOffering(courseOfferingRepository.getReferenceById(courseOfferingId))
            .examDate(examDate)
            .coefficient(coefficient)
            .build();

    return examMapper.toModel(examRepository.save(entity));
  }

  public void delete(UUID id) {
    if (!examRepository.existsById(id)) {
      throw ResourceNotFoundException.of("Exam", id);
    }

    examRepository.deleteById(id);
  }
}
