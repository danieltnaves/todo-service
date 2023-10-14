package com.danieltnaves.todo.todo.domain;

import com.danieltnaves.todo.todo.api.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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

    private String description;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime dueAt;

}
