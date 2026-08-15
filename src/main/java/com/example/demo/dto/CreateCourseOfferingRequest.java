package com.example.demo.dto;

import java.util.UUID;

public record CreateCourseOfferingRequest(UUID courseId, UUID academicYearId, UUID groupId) {}
