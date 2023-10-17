package com.danieltnaves.todoservice.todo.errors;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends GenericHttpException {

    public InvalidInputException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
