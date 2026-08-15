package com.example.demo.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

/** Levee quand une entite identifiee par son id n'existe pas. Traduite en 404. */
public class ResourceNotFoundException extends ApiException {

  public ResourceNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }

  public static ResourceNotFoundException of(String resourceName, UUID id) {
    return new ResourceNotFoundException(resourceName + " introuvable: " + id);
  }
}
