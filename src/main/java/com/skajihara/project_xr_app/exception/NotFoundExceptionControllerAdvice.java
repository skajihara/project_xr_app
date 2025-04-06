package com.skajihara.project_xr_app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NotFoundExceptionControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(NotFoundExceptionControllerAdvice.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleAccountException(NotFoundException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
