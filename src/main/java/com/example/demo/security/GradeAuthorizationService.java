package com.example.demo.security;

import com.example.demo.entity.JUser;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.TeacherAssignmentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GradeAuthorizationService {

  private final ExamRepository examRepository;
  private final TeacherAssignmentRepository teacherAssignmentRepository;

  public boolean canGrade(UUID examId, Authentication authentication) {
    var me = (JUser) authentication.getPrincipal();

    if (me.getRole() == UserRole.ADMIN) {
      return true;
    }

    if (me.getRole() != UserRole.TEACHER) {
      return false;
    }

    var exam =
        examRepository.findById(examId).orElseThrow(() -> NotFoundException.of("Examen", examId));

    return teacherAssignmentRepository
        .findByCourseOffering_Id(exam.getCourseOffering().getId())
        .stream()
        .anyMatch(teacherAssignment -> teacherAssignment.getTeacher().getId().equals(me.getId()));
  }
}
