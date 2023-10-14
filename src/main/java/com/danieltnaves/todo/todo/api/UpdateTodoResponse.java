package com.danieltnaves.todo.todo.api;

import java.time.LocalDateTime;

public record UpdateTodoResponse(Status status, String description, LocalDateTime updatedAt) {
}
