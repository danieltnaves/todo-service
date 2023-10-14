package com.danieltnaves.todo.todo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "description is required")
    @Size(min = 1, max = 200, message = "Description must be between 2 and 200 characters")
    private String description;

    @NotBlank(message = "status is required")
    private Status status;

    @NotBlank(message = "createdAt is required")
    private LocalDateTime createdAt;

    private LocalDateTime doneAt;

    private LocalDateTime dueAt;

    public enum Status {

        NOT_DONE, DONE, PAST_DUE;

        public static Status fromString(String text) {
            for (Status status : Todo.Status.values()) {
                if (status.name().equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException(String.format("No constant with text %s found", text));
        }

    }
}
