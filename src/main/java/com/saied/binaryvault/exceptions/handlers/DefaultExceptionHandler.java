package com.saied.binaryvault.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<?> handleException(
        InsufficientAuthenticationException e,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                new HandlerResponseTemplate(
                    ZonedDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase(),
                    e,
                    request.getRequestURI()
                )
            );
    }

}
