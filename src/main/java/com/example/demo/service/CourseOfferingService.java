package com.example.demo.service;

import com.example.demo.entity.JCourseOffering;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CourseOfferingMapper;
import com.example.demo.model.CourseOffering;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.CourseOfferingRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.GroupRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseOfferingService {

  private final CourseOfferingRepository courseOfferingRepository;
  private final CourseRepository courseRepository;
  private final AcademicYearRepository academicYearRepository;
  private final GroupRepository groupRepository;
  private final CourseOfferingMapper courseOfferingMapper;

  public List<CourseOffering> findAll() {
    return courseOfferingRepository.findAll().stream().map(courseOfferingMapper::toModel).toList();
  }

  public List<CourseOffering> findByAcademicYear(UUID academicYearId) {
    return courseOfferingRepository.findByAcademicYear_Id(academicYearId).stream()
        .map(courseOfferingMapper::toModel)
        .toList();
  }

  public List<CourseOffering> findByGroupAndAcademicYear(UUID groupId, UUID academicYearId) {
    return courseOfferingRepository
        .findByGroup_IdAndAcademicYear_Id(groupId, academicYearId)
        .stream()
        .map(courseOfferingMapper::toModel)
        .toList();
  }

  public CourseOffering findById(UUID id) {
    return courseOfferingRepository
        .findById(id)
        .map(courseOfferingMapper::toModel)
        .orElseThrow(() -> ResourceNotFoundException.of("Course offering", id));
  }

  public CourseOffering create(UUID courseId, UUID academicYearId, UUID groupId) {
    if (!courseRepository.existsById(courseId)) {
      throw ResourceNotFoundException.of("Course", courseId);
    }

    if (!academicYearRepository.existsById(academicYearId)) {
      throw ResourceNotFoundException.of("Academic year", academicYearId);
    }

    if (!groupRepository.existsById(groupId)) {
      throw ResourceNotFoundException.of("Group", groupId);
    }

    courseOfferingRepository
        .findByCourse_IdAndAcademicYear_IdAndGroup_Id(courseId, academicYearId, groupId)
        .ifPresent(
            existing -> {
              throw new ConflictException(
                  "This course is already assigned to this group for this academic year");
            });

    var entity =
        JCourseOffering.builder()
            .course(courseRepository.getReferenceById(courseId))
            .academicYear(academicYearRepository.getReferenceById(academicYearId))
            .group(groupRepository.getReferenceById(groupId))
            .build();

    return courseOfferingMapper.toModel(courseOfferingRepository.save(entity));
  }

  public void delete(UUID id) {
    if (!courseOfferingRepository.existsById(id)) {
      throw ResourceNotFoundException.of("Course offering", id);
    }

    courseOfferingRepository.deleteById(id);
  }
}
