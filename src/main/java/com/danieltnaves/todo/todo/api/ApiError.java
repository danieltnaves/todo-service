package com.danieltnaves.todo.todo.api;

import org.springframework.http.HttpStatus;

public record ApiError(HttpStatus httpStatus, String message, String description) {
}
