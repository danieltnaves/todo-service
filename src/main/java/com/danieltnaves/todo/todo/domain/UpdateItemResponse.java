package com.danieltnaves.todo.todo.domain;

import java.time.ZonedDateTime;

public record UpdateItemResponse(Status status, String description, ZonedDateTime updatedAt) {
}
