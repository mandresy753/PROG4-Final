package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Levee quand un utilisateur authentifie, avec un role autorise a appeler l'endpoint, tente d'agir
 * sur une ressource qui ne lui appartient pas (ex: un TEACHER qui note un cours qu'il n'enseigne
 * pas, un STUDENT qui consulte les notes d'un autre etudiant).
 *
 * <p>A ne pas confondre avec le RBAC "qui a le droit d'appeler cet endpoint", qui reste gere par
 * Spring Security ({@code SecurityConfig} + {@code @PreAuthorize}) et ne doit jamais etre duplique
 * ici.
 */
public class ForbiddenException extends ApiException {

  public ForbiddenException(String message) {
    super(HttpStatus.FORBIDDEN, message);
  }
}
