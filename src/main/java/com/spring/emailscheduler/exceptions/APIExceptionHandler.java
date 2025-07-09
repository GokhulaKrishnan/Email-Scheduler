package com.spring.emailscheduler.exceptions;

public class APIExceptionHandler extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public APIExceptionHandler() {
    }

    public APIExceptionHandler(String message) {
        super(message);
    }
}
