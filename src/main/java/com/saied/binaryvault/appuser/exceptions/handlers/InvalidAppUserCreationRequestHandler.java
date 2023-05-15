package com.saied.binaryvault.appuser.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvalidAppUserCreationRequestHandler {

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
