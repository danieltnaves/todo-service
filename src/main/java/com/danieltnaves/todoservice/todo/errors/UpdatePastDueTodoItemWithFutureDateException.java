package com.danieltnaves.todoservice.todo.errors;

public class UpdatePastDueTodoItemWithFutureDateException extends RuntimeException {

    public UpdatePastDueTodoItemWithFutureDateException(String message) {
        super(message);
    }

}
