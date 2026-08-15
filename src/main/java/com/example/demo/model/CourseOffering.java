package com.example.demo.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CourseOffering(UUID id, Course course, AcademicYear academicYear, Group group) {}