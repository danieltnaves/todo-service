package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.*;
import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TodoService {

    private final ChangeStatusVisitor changeStatusVisitor;

    private final TodoRepository todoRepository;

    public TodoService(ChangeStatusVisitor changeStatusVisitor, TodoRepository todoRepository) {
        this.changeStatusVisitor = changeStatusVisitor;
        this.todoRepository = todoRepository;
    }

    public UpdateTodoResponse updateTodo(Long id, UpdateTodoRequest updateTodoRequest) {
        return updateTodoRequest.accept(changeStatusVisitor);
    }

    public GetTodosResponse getTodosByFilter(Status status) {
        return null;
    }

    public GetTodosResponse getTodoById(Long id) {
        return null;
    }

    public AddTodoResponse addTodo(AddTodoRequest addTodoRequest) {
        if (addTodoRequest == null || ObjectUtils.isEmpty(addTodoRequest.description())) {
            throw new InvalidInputException("The Todo description wasn't provided");
        }
        return AddTodoResponse.convertFromTodoToAddTodoResponse(todoRepository.save(Todo.builder()
                .description(addTodoRequest.description())
                .status(Status.NOT_DONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));
    }
}
