package com.danieltnaves.todoservice.todo.errors.configuration;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.danieltnaves.todoservice.todo.errors.GenericHttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GenericHttpException.class)
    public ResponseEntity<Object> handleGenericHttpException(GenericHttpException exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, exception.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<Object> getObjectResponseEntity(Exception exception, WebRequest request, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiError(httpStatus, exception.getMessage(), request.getDescription(false)), httpStatus);
    }

}
