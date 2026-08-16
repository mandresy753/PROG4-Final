package com.example.demo.security;

import com.example.demo.entity.JUser;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.TeacherAssignmentRepository;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
public class GradeReadAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {

  private final ExamRepository examRepository;
  private final TeacherAssignmentRepository teacherAssignmentRepository;

  public GradeReadAuthorizationManager(
      ExamRepository examRepository,
      TeacherAssignmentRepository teacherAssignmentRepository) {
    this.examRepository = examRepository;
    this.teacherAssignmentRepository = teacherAssignmentRepository;
  }

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
    Authentication authentication = authenticationSupplier.get();

    if (!(authentication.getPrincipal() instanceof JUser me)) {
      return new AuthorizationDecision(false);
    }

    if (me.getRole() == UserRole.ADMIN) {
      return new AuthorizationDecision(true);
    }

    String studentIdParam = context.getRequest().getParameter("studentId");
    if (studentIdParam == null) {
      return new AuthorizationDecision(false);
    }

    if (me.getRole() == UserRole.STUDENT) {
      return new AuthorizationDecision(studentIdParam.equals(me.getId().toString()));
    }

    if (me.getRole() != UserRole.TEACHER) {
      return new AuthorizationDecision(false);
    }

    String examIdParam = context.getRequest().getParameter("examId");
    if (examIdParam != null) {
      return canTeacherAccessExam(me.getId(), examIdParam);
    }

    String courseOfferingIdParam = context.getRequest().getParameter("courseOfferingId");
    if (courseOfferingIdParam == null) {
      return new AuthorizationDecision(false);
    }

    return canTeacherAccessCourseOffering(me.getId(), courseOfferingIdParam);
  }

  private AuthorizationDecision canTeacherAccessExam(UUID teacherId, String examIdParam) {
    try {
      UUID examId = UUID.fromString(examIdParam);
      return new AuthorizationDecision(
          examRepository
              .findById(examId)
              .map(exam -> canTeacherAccessCourseOfferingValue(
                  teacherId, exam.getCourseOffering().getId()))
              .orElse(false));
    } catch (IllegalArgumentException e) {
      return new AuthorizationDecision(false);
    }
  }

  private AuthorizationDecision canTeacherAccessCourseOffering(
      UUID teacherId, String courseOfferingIdParam) {
    try {
      UUID courseOfferingId = UUID.fromString(courseOfferingIdParam);
      return new AuthorizationDecision(
          canTeacherAccessCourseOfferingValue(teacherId, courseOfferingId));
    } catch (IllegalArgumentException e) {
      return new AuthorizationDecision(false);
    }
  }

  private boolean canTeacherAccessCourseOfferingValue(
      UUID teacherId, UUID courseOfferingId) {
    return teacherAssignmentRepository.findByCourseOffering_Id(courseOfferingId).stream()
        .anyMatch(
            assignment -> assignment.getTeacher().getId().equals(teacherId));
  }
}
