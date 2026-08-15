package com.example.demo.service;

import com.example.demo.entity.JUser;
import com.example.demo.enums.Track;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.BadRequestException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.report.Graduate;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GraduationService {

  public static final int EXPECTED_TOTAL_CREDITS =
      SemesterCreditPolicy.MAX_CREDITS_PER_SEMESTER * 6;

  private final UserRepository userRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final GradeAverageService gradeAverageService;
  private final UserMapper userMapper;

  public List<Graduate> listGraduates(Track track) {
    return userRepository.findByRole(UserRole.STUDENT).stream()
        .map(student -> new StudentTrack(student, studentTrack(student.getId())))
        .filter(studentTrack -> studentTrack.track() == track)
        .map(studentTrack -> evaluate(studentTrack.student(), studentTrack.track()))
        .filter(Graduate::graduated)
        .toList();
  }

  private Graduate evaluate(JUser student, Track track) {
    var overall = gradeAverageService.overallAverage(student.getId());

    var graduated =
        overall.complete()
            && overall.totalCredits() >= EXPECTED_TOTAL_CREDITS
            && overall.years().stream()
                .allMatch(year -> year.validatedCredits() == year.totalCredits());

    return Graduate.builder()
        .student(userMapper.toModel(student))
        .track(track)
        .overallAverage(overall.overallAverage())
        .totalCredits(overall.totalCredits())
        .graduated(graduated)
        .build();
  }

  private Track studentTrack(UUID studentId) {
    var tracks =
        enrollmentRepository.findByStudent_Id(studentId).stream()
            .map(enrollment -> enrollment.getGroup().getTrack())
            .collect(Collectors.toSet());

    if (tracks.size() != 1) {
      throw new BadRequestException(
          "Student " + studentId + " has an inconsistent or undefined track");
    }

    return tracks.iterator().next();
  }

  private record StudentTrack(JUser student, Track track) {}
}
