package com.danieltnaves.todo.todo.api;

import java.time.LocalDateTime;

public record TodoItemResponse(String description, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime dueAt) {
}
