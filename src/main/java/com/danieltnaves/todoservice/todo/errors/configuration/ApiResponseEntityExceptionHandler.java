package com.danieltnaves.todoservice.todo.errors.configuration;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.danieltnaves.todoservice.todo.errors.InvalidInputException;
import com.danieltnaves.todoservice.todo.errors.TodoItemNotFoundException;
import com.danieltnaves.todoservice.todo.errors.UpdateDoneTodoItemException;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueTodoItemException;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueTodoItemWithFutureDateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TodoItemNotFoundException.class)
    public ResponseEntity<Object> handleTodoItemNotFoundException(TodoItemNotFoundException exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, NOT_FOUND);
    }

    @ExceptionHandler(UpdateDoneTodoItemException.class)
    public ResponseEntity<Object> handleUpdateDoneTodoItemException(UpdateDoneTodoItemException exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, BAD_REQUEST);
    }

    @ExceptionHandler(UpdatePastDueTodoItemException.class)
    public ResponseEntity<Object> handleUpdatePastDueTodoItemException(UpdatePastDueTodoItemException exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputException(InvalidInputException exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, BAD_REQUEST);
    }

    @ExceptionHandler(UpdatePastDueTodoItemWithFutureDateException.class)
    public ResponseEntity<Object> handleUpdatePastDueTodoItemWithFutureDateException(UpdatePastDueTodoItemWithFutureDateException exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception, WebRequest request) {
        return getObjectResponseEntity(exception, request, INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<Object> getObjectResponseEntity(Exception exception, WebRequest request, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiError(httpStatus, exception.getMessage(), request.getDescription(false)), httpStatus);
    }

}
