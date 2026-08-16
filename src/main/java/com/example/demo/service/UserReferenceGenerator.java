package com.example.demo.service;

import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Generates the business identifier (matricule) shown to users, distinct from the internal UUID
 * primary key. Format: <role prefix><4-digit sequence>, e.g. STD0001, TCH0012, ADM0003. The
 * sequence is scoped per role prefix and derived from the highest existing reference for that
 * prefix, so each role has its own numbering.
 */
@Component
@AllArgsConstructor
public class UserReferenceGenerator {

  private static final int SEQUENCE_DIGITS = 4;

  private final UserRepository userRepository;

  public String generate(UserRole role) {
    var prefix = role.referencePrefix();

    var nextSequence =
        userRepository
            .findFirstByReferenceStartingWithOrderByReferenceDesc(prefix)
            .map(user -> extractSequence(user.getReference(), prefix) + 1)
            .orElse(1);

    return prefix + String.format("%0" + SEQUENCE_DIGITS + "d", nextSequence);
  }

  private int extractSequence(String reference, String prefix) {
    try {
      return Integer.parseInt(reference.substring(prefix.length()));
    } catch (NumberFormatException e) {
      // Defensive fallback: an unexpected reference shape shouldn't block user creation.
      return 0;
    }
  }
}
