package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateExamRequest(
    UUID courseOfferingId, LocalDateTime examDate, BigDecimal coefficient) {}
