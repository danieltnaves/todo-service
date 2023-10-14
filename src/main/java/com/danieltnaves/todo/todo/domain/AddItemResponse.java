package com.danieltnaves.todo.todo.domain;


import java.time.ZonedDateTime;

public record AddItemResponse(Status status, ZonedDateTime createdAt) {
}
