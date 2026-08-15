package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/** Levee quand l'operation entre en conflit avec l'etat actuel des donnees. Traduite en 409. */
public class ConflictException extends ApiException {

  public ConflictException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
