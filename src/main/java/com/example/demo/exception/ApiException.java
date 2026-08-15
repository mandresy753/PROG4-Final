package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Racine de toutes les exceptions "metier" de l'application.
 *
 * <p>Idee : les services ne connaissent pas Spring Web (pas de {@code ResponseStatusException}, pas
 * de {@code HttpStatus} disperse dans la logique metier). Ils levent une exception metier explicite
 * (ex: {@link ResourceNotFoundException}), et c'est {@link GlobalExceptionHandler}, en un seul
 * endroit, qui sait traduire ça en reponse HTTP. Ca respecte SRP : le service decide "quoi", le
 * handler decide "comment le dire au client HTTP".
 */
public abstract class ApiException extends RuntimeException {

  private final HttpStatus status;

  protected ApiException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
