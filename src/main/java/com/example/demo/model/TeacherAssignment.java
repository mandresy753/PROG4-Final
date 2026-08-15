package com.example.demo.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TeacherAssignment(UUID id, CourseOffering courseOffering, User teacher) {}