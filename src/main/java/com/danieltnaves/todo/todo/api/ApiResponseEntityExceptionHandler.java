package com.danieltnaves.todo.todo.api;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TodoItemNotFoundException.class)
    public ResponseEntity<Object> handleTodoItemNotFoundException(TodoItemNotFoundException exception, WebRequest request) {
        return new ResponseEntity<>(new ApiError(NOT_FOUND, exception.getMessage(), request.getDescription(false)), NOT_FOUND);
    }

    @ExceptionHandler(UpdateDoneTodoItemException.class)
    public ResponseEntity<Object> handleUpdateDoneTodoItemException(UpdateDoneTodoItemException exception, WebRequest request) {
        return new ResponseEntity<>(new ApiError(BAD_REQUEST, exception.getMessage(), request.getDescription(false)), BAD_REQUEST);
    }

    @ExceptionHandler(UpdatePastDueTodoItemException.class)
    public ResponseEntity<Object> handleUpdatePastDueTodoItemException(UpdatePastDueTodoItemException exception, WebRequest request) {
        return new ResponseEntity<>(new ApiError(BAD_REQUEST, exception.getMessage(), request.getDescription(false)), BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputException(InvalidInputException exception, WebRequest request) {
        return new ResponseEntity<>(new ApiError(BAD_REQUEST, exception.getMessage(), request.getDescription(false)), BAD_REQUEST);
    }

    @ExceptionHandler(UpdatePastDueTodoItemWithFutureDateException.class)
    public ResponseEntity<Object> handleUpdatePastDueTodoItemWithFutureDateException(UpdatePastDueTodoItemWithFutureDateException exception, WebRequest request) {
        return new ResponseEntity<>(new ApiError(BAD_REQUEST, exception.getMessage(), request.getDescription(false)), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception, WebRequest request) {
        return new ResponseEntity<>(new ApiError(INTERNAL_SERVER_ERROR, exception.getMessage(), request.getDescription(false)), INTERNAL_SERVER_ERROR);
    }
}
