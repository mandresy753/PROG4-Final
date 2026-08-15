package com.example.demo.service;

import com.example.demo.entity.JEnrollment;
import com.example.demo.enums.Level;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.EnrollmentMapper;
import com.example.demo.model.Enrollment;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EnrollmentService {

  private final EnrollmentRepository enrollmentRepository;
  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final AcademicYearRepository academicYearRepository;
  private final EnrollmentMapper enrollmentMapper;

  public List<Enrollment> findByStudent(UUID studentId) {
    return enrollmentRepository.findByStudent_IdOrderByStartDateAsc(studentId).stream()
        .map(enrollmentMapper::toModel)
        .toList();
  }

  public List<Enrollment> findByGroupAndAcademicYear(UUID groupId, UUID academicYearId) {
    return enrollmentRepository.findByGroup_IdAndAcademicYear_Id(groupId, academicYearId).stream()
        .map(enrollmentMapper::toModel)
        .toList();
  }

  public Enrollment create(
      UUID studentId,
      UUID groupId,
      UUID academicYearId,
      Level level,
      LocalDate startDate,
      LocalDate endDate) {

    var student =
        userRepository
            .findById(studentId)
            .orElseThrow(() -> ResourceNotFoundException.of("Student", studentId));

    if (student.getRole() != UserRole.STUDENT) {
      throw new BadRequestException("This user is not a student");
    }

    if (!groupRepository.existsById(groupId)) {
      throw ResourceNotFoundException.of("Group", groupId);
    }

    if (!academicYearRepository.existsById(academicYearId)) {
      throw ResourceNotFoundException.of("Academic year", academicYearId);
    }

    var entity =
        JEnrollment.builder()
            .student(userRepository.getReferenceById(studentId))
            .group(groupRepository.getReferenceById(groupId))
            .academicYear(academicYearRepository.getReferenceById(academicYearId))
            .level(level)
            .startDate(startDate)
            .endDate(endDate)
            .build();

    return enrollmentMapper.toModel(enrollmentRepository.save(entity));
  }

  public void delete(UUID id) {
    if (!enrollmentRepository.existsById(id)) {
      throw ResourceNotFoundException.of("Enrollment", id);
    }

    enrollmentRepository.deleteById(id);
  }
}
