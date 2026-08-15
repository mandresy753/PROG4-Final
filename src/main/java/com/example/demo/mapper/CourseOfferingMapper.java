package com.example.demo.mapper;

import com.example.demo.entity.JCourseOffering;
import com.example.demo.model.CourseOffering;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CourseOfferingMapper {

  private final CourseMapper courseMapper;
  private final AcademicYearMapper academicYearMapper;
  private final GroupMapper groupMapper;

  public CourseOffering toModel(JCourseOffering entity) {
    return CourseOffering.builder()
        .id(entity.getId())
        .course(courseMapper.toModel(entity.getCourse()))
        .academicYear(academicYearMapper.toModel(entity.getAcademicYear()))
        .group(groupMapper.toModel(entity.getGroup()))
        .build();
  }

  public JCourseOffering toEntity(CourseOffering model) {
    return JCourseOffering.builder()
        .id(model.id())
        .course(courseMapper.toEntity(model.course()))
        .academicYear(academicYearMapper.toEntity(model.academicYear()))
        .group(groupMapper.toEntity(model.group()))
        .build();
  }
}
