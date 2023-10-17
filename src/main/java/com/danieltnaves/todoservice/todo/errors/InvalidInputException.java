package com.danieltnaves.todoservice.todo.errors;

public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }

}
