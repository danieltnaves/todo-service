package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByStatus(Todo.Status status);
}
