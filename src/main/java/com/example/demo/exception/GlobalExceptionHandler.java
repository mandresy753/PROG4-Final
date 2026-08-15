package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Point unique de traduction des exceptions en reponses HTTP.
 *
 * <p>Les controllers et services ne construisent plus jamais de {@code ResponseStatusException} ou
 * de reponse d'erreur "a la main" : ils levent une {@link ApiException} et c'est ce handler qui
 * decide du format de reponse. Ca centralise la mise en forme (utile le jour ou on veut, par
 * exemple, logger toutes les erreurs 403/404 au meme endroit) et ca evite la duplication qu'on
 * avait dans chaque service.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  public record ApiError(
      Instant timestamp, int status, String error, String message, String path) {}

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
    return build(ex.getStatus(), ex.getMessage(), request);
  }

  /** Refus emis par @PreAuthorize / @EnableMethodSecurity (RBAC par role). */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return build(HttpStatus.FORBIDDEN, "Acces refuse pour ce role", request);
  }

  private ResponseEntity<ApiError> build(
      HttpStatus status, String message, HttpServletRequest request) {
    var body =
        new ApiError(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI());
    return ResponseEntity.status(status).body(body);
  }
}
