package com.danieltnaves.todoservice.todo.errors;

public class UpdatePastDueTodoItemException extends RuntimeException {

    public UpdatePastDueTodoItemException(String message) {
        super(message);
    }

}
