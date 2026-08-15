package com.example.demo.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record AcademicYear(UUID id, String label, LocalDate startDate, LocalDate endDate) {}