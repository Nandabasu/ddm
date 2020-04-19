package com.ddm.authorizationserver.exception;

import org.springframework.http.HttpStatus;

public class DdmException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String errorCode;
    private final HttpStatus httpStatus;

    public DdmException(final String errorCode, final String errorMessage, HttpStatus httpStatus) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}