package com.ddm.authorizationserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ddm.authorizationserver.response.ErrorResponse;

@ControllerAdvice
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DdmException.class)
    public final ResponseEntity<Object> handleAllExceptions(DdmException ex) {
        ErrorResponse exceptionResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity(exceptionResponse, ex.getHttpStatus());
    }
}