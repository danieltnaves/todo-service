package com.danieltnaves.todoservice.todo.errors;

import org.springframework.http.HttpStatus;

public class TodoItemNotFoundException extends GenericHttpException {

    public TodoItemNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
