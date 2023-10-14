package com.danieltnaves.todo.todo.api;


import com.danieltnaves.todo.todo.domain.Todo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TodoDTO(Long id, String description, Status status, LocalDateTime cratedAt, LocalDateTime updatedAt, LocalDateTime dueAt) {

    public enum Status {
        NOT_DONE, DONE, PAST_DUE;

        public static Status fromString(String text) {
            for (Status status : Status.values()) {
                if (status.name().equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }

    public static TodoDTO fromTodoToTodoDTO(Todo todo) {
        return new TodoDTO(todo.getId(), todo.getDescription(), Status.fromString(todo.getStatus().name()), todo.getCreatedAt(), todo.getUpdatedAt(), todo.getDueAt());
    }

    public static Todo fromTodoDTOToTodo(TodoDTO todoDTO) {
        return new Todo(todoDTO.id(), todoDTO.description, Todo.Status.fromString(todoDTO.status.name()), todoDTO.cratedAt(), todoDTO.updatedAt(), todoDTO.dueAt());
    }
}


