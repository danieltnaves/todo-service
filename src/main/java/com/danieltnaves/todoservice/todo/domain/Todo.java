package com.danieltnaves.todoservice.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TODO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    private Status status;

    @NotNull
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "DONE_AT")
    private LocalDateTime doneAt;

    @Column(name = "DUE_AT")
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
