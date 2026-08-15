package com.example.demo.model;

import com.example.demo.enums.Level;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record Enrollment(
        UUID id, User student, Group group, AcademicYear academicYear,
        Level level, LocalDate startDate, LocalDate endDate) {}