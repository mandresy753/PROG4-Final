package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/** Levee quand l'authentification echoue (identifiants invalides). Traduite en 401. */
public class UnauthorizedException extends ApiException {

  public UnauthorizedException(String message) {
    super(HttpStatus.UNAUTHORIZED, message);
  }
}
