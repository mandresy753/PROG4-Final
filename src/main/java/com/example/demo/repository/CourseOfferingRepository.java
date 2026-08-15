package com.example.demo.repository;

import com.example.demo.entity.JCourseOffering;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseOfferingRepository extends JpaRepository<JCourseOffering, UUID> {

  List<JCourseOffering> findByAcademicYear_Id(UUID academicYearId);

  List<JCourseOffering> findByGroup_IdAndAcademicYear_Id(UUID groupId, UUID academicYearId);

  List<JCourseOffering> findByCourse_Id(UUID courseId);

  Optional<JCourseOffering> findByCourse_IdAndAcademicYear_IdAndGroup_Id(
      UUID courseId, UUID academicYearId, UUID groupId);
}
