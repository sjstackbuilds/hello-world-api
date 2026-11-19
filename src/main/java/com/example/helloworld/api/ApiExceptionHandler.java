package com.example.helloworld.api;

import com.example.helloworld.service.InvalidNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps domain exceptions to HTTP responses so controllers stay free of error-handling code. */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(InvalidNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidName(InvalidNameException ex) {
        log.debug("Rejected request: {}", ex.getMessage());
        return ErrorResponse.invalidInput();
    }
}
