package com.danieltnaves.todoservice.todo.errors;

public class TodoItemNotFoundException extends RuntimeException {

    public TodoItemNotFoundException(String message) {
        super(message);
    }

}
