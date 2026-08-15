package com.example.demo.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Grade(UUID id, Exam exam, User student, BigDecimal value,
                    LocalDateTime entryDate, User enteredBy, String reason) {}