package com.saied.binaryvault.exceptions.appuser;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AppUserAlreadyExistsException extends RuntimeException {

    public AppUserAlreadyExistsException(String message) {
        super(message);
    }
}
