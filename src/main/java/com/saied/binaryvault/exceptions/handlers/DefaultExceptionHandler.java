package com.saied.binaryvault.exceptions.handlers;

import com.saied.binaryvault.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(
        ResourceNotFoundException e,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                new HandlerResponseTemplate(
                    ZonedDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    e.getMessage(),
                    request.getRequestURI()
                )
            );
    }



    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<?> handleInsufficientAuthenticationException(
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
                    e.getMessage(),
                    request.getRequestURI()
                )
            );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidAppUserCreationRequest(
        MethodArgumentNotValidException e,
        HttpServletRequest request
    ) {
        Map<String, String> payload = new HashMap<>();
        BindingResult res = e.getBindingResult();
        List<FieldError> fieldErrors = res.getFieldErrors();
        fieldErrors.forEach(
            fieldError -> payload.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return ResponseEntity
            .badRequest()
            .body(
                new HandlerResponseTemplate(
                    ZonedDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    payload,
                    request.getRequestURI()
                )
            );
    }

}
