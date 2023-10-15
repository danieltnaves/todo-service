package com.danieltnaves.todo.todo.api;

public class UpdatePastDueTodoItemWithFutureDateException extends RuntimeException {

    public UpdatePastDueTodoItemWithFutureDateException(String message) {
        super(message);
    }

}
