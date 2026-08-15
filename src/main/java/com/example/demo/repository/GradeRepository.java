package com.example.demo.repository;

import com.example.demo.entity.JGrade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<JGrade, UUID> {

  List<JGrade> findByStudent_Id(UUID studentId);

  List<JGrade> findByExam_Id(UUID examId);

  List<JGrade> findByExam_IdAndStudent_IdOrderByEntryDateDesc(UUID examId, UUID studentId);

  default Optional<JGrade> findLatestByExamAndStudent(UUID examId, UUID studentId) {
    return findByExam_IdAndStudent_IdOrderByEntryDateDesc(examId, studentId).stream().findFirst();
  }
}
