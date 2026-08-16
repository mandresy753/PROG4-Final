package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Projection for {@link GraduationQueryRepository#findRankedGraduates}. One row per graduated
 * student, already averaged, filtered and ranked entirely in SQL.
 */
public interface GraduateRankingRow {

  UUID getId();

  String getReference();

  String getLastName();

  String getFirstName();

  String getEmail();

  BigDecimal getOverallAverage();

  Integer getTotalCredits();

  Integer getRank();
}
