package com.saied.binaryvault.appuser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AppUserNotFoundException extends RuntimeException {

    public AppUserNotFoundException(String message) {
        super(message);
    }
}
