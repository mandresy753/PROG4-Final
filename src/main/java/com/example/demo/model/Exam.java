package com.example.demo.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record Exam(UUID id, CourseOffering courseOffering, LocalDate examDate, BigDecimal coefficient) {}