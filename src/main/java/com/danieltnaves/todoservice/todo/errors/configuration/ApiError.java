package com.danieltnaves.todoservice.todo.errors.configuration;

import org.springframework.http.HttpStatus;

public record ApiError(HttpStatus httpStatus, String message, String description) {
}
