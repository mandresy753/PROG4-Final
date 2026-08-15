package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/** Levee quand la requete est mal formee ou viole une regle de validation. Traduite en 400. */
public class BadRequestException extends ApiException {

  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
