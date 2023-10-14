package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
