package com.danieltnaves.todoservice.todo.errors;

import org.springframework.http.HttpStatus;

public class UpdatePastDueException extends GenericHttpException {

    public UpdatePastDueException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
