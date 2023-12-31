package com.danieltnaves.todoservice.todo.errors;

import org.springframework.http.HttpStatus;

public class UpdateDoneTodoItemException extends GenericHttpException {

    public UpdateDoneTodoItemException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
