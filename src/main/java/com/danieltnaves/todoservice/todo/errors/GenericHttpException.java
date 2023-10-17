package com.danieltnaves.todoservice.todo.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GenericHttpException extends RuntimeException {

    private final HttpStatus httpStatus;

    public GenericHttpException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
