package com.danieltnaves.todo.todo.api;


import com.danieltnaves.todo.todo.domain.Todo;

import java.time.LocalDateTime;

public record AddTodoResponse(Long id, String description, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime dueAt) {

    public static AddTodoResponse convertFromTodoToAddTodoResponse(Todo todo) {
        return new AddTodoResponse(todo.getId(), todo.getDescription(), todo.getStatus(), todo.getCreatedAt(), todo.getUpdatedAt(), todo.getDueAt());
    }

    public static Todo convertFromAddTodoResponseToTodo(AddTodoResponse todo) {
        return new Todo(todo.id(), todo.description(), todo.status(), todo.createdAt(), todo.updatedAt(), todo.dueAt());
    }
}
