package com.example.demo.security;

import com.example.demo.PojaGenerated;
import com.example.demo.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Reponse 403 renvoyee par la security filter chain (utilisateur authentifie mais role insuffisant,
 * ex: hasRole('ADMIN') qui echoue). Meme format que {@link
 * com.example.demo.exception.GlobalExceptionHandler} pour les AccessDeniedException levees plus
 * tard (ex: GradeAuthorizationService), pour une experience API homogene.
 */
@PojaGenerated
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
      throws java.io.IOException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    var body =
        new ErrorResponse(
            Instant.now(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            "Acces refuse",
            request.getRequestURI());
    objectMapper.writeValue(response.getWriter(), body);
  }
}
