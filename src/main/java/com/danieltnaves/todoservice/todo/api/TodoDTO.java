package com.danieltnaves.todoservice.todo.api;


import com.danieltnaves.todoservice.todo.domain.Todo;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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


