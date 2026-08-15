package com.example.demo.repository;

import com.example.demo.entity.JEnrollment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<JEnrollment, UUID> {

  List<JEnrollment> findByStudent_Id(UUID studentId);

  List<JEnrollment> findByStudent_IdOrderByStartDateAsc(UUID studentId);

  List<JEnrollment> findByGroup_IdAndAcademicYear_Id(UUID groupId, UUID academicYearId);

  List<JEnrollment> findByAcademicYear_Id(UUID academicYearId);
}
