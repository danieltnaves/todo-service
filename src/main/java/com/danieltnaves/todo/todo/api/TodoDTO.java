package com.danieltnaves.todo.todo.api;


import com.danieltnaves.todo.todo.domain.Todo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TodoDTO(Long id, String description, Status status, LocalDateTime createdAt, LocalDateTime doneAt, LocalDateTime dueAt) {

    public enum Status {
        NOT_DONE, DONE, PAST_DUE;

        public static Status fromString(String text) {
            for (Status status : Status.values()) {
                if (status.name().equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException(String.format("No constant with text %s found", text));
        }
    }

    public static TodoDTO fromTodoToTodoDTO(Todo todo) {
        return new TodoDTO(todo.getId(), todo.getDescription(), Status.fromString(todo.getStatus().name()), todo.getCreatedAt(), todo.getDoneAt(), todo.getDueAt());
    }

    public static Todo fromTodoDTOToTodo(TodoDTO todoDTO) {
        return new Todo(todoDTO.id(), todoDTO.description, Todo.Status.fromString(todoDTO.status.name()), todoDTO.createdAt(), todoDTO.doneAt(), todoDTO.dueAt());
    }
}


