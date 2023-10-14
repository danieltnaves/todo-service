package com.danieltnaves.todo.todo.domain;

import java.time.ZonedDateTime;

public record ItemResponse(String description, Status status, ZonedDateTime createdAt, ZonedDateTime updatedAt, ZonedDateTime dueAt) {
}
