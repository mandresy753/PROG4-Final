package com.example.demo.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CourseOffering(UUID id, Course course, AcademicYear academicYear, Group group) {}
