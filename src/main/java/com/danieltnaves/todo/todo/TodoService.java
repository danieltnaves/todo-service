package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.*;
import com.danieltnaves.todo.todo.api.TodoDTO.Status;
import com.danieltnaves.todo.todo.domain.Todo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoDTO updateTodo(Long id, TodoDTO updateTodoRequest) {
        return null;
    }

    public TodoDTO getTodosByFilter(Status status) {
        return null;
    }

    public TodoDTO getTodoById(Long id) {
        return null;
    }

    public TodoDTO addTodo(TodoDTO todoDTO) {
        if (ObjectUtils.isEmpty(todoDTO) || ObjectUtils.isEmpty(todoDTO.description())) {
            throw new InvalidInputException("The Todo description wasn't provided");
        }
        return TodoDTO.fromTodoToTodoDTO(todoRepository.save(Todo.builder()
                .description(todoDTO.description())
                .status(Todo.Status.NOT_DONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));
    }
}
