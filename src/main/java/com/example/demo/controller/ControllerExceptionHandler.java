package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /**
     * We would want to handle 404 separately since it's client error. This way, when we monitor our service for error,
     * there's no need to cut a high severity alarm when client error is high.
     * @param e
     * @return
     */
    @ExceptionHandler({WebClientResponseException.NotFound.class})
    public ResponseEntity<Object> handleResourceNotFoundException(Exception e) {
        log.error("exception", e);
        return new ResponseEntity<>("Resource Not Found", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
