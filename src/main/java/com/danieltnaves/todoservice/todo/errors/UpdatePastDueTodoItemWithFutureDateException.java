package com.danieltnaves.todoservice.todo.errors;

import org.springframework.http.HttpStatus;

public class UpdatePastDueTodoItemWithFutureDateException extends GenericHttpException {

    public UpdatePastDueTodoItemWithFutureDateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
