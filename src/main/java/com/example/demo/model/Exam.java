package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Exam(
    UUID id, CourseOffering courseOffering, LocalDateTime examDate, BigDecimal coefficient) {}
