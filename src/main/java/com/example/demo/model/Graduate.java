package com.example.demo.model;

import com.example.demo.enums.Track;
import com.example.demo.model.User;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record Graduate(User student, Track track, BigDecimal overallAverage, int totalCredits, boolean graduated) {}