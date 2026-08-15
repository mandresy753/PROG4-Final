package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateExamRequest(
    UUID courseOfferingId, LocalDate examDate, BigDecimal coefficient) {}
