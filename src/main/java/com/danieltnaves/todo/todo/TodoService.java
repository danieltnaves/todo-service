package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.*;
import com.danieltnaves.todo.todo.api.TodoDTO.Status;
import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Service
public class TodoService {

    public static final String TODO_ITEM_NOT_FOUND_MESSAGE = "The Todo item with the ID %d wasn't found";

    public static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to update the information for the Todo id %d. because it's a past due item";

    public static final String TODO_ITEM_MARKED_AS_DONE_MESSAGE = "The Todo item %d was already marked as DONE. It needs to be updated to NOT_DONE before performing this operation";

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional
    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoItemNotFoundException(String.format(TODO_ITEM_NOT_FOUND_MESSAGE, id)));

        if (isDueTodoItem(todo)) {
            throw new UpdatePastDueTodoItemException(String.format(PAST_DUE_TODO_ITEM_MESSAGE, id));
        }

        if (isUpdateOperationAllowed(todoDTO, todo)) {
            throw new UpdateDoneTodoItemException(String.format(TODO_ITEM_MARKED_AS_DONE_MESSAGE, id));
        }

        todo.setStatus(!ObjectUtils.isEmpty(todoDTO.status()) ? Todo.Status.fromString(todoDTO.status().name()) : todo.getStatus());
        todo.setDescription(!ObjectUtils.isEmpty(todoDTO.description()) ? todoDTO.description() : todo.getDescription());
        todo.setDoneAt(Status.DONE.equals(todoDTO.status()) ? LocalDateTime.now() : todo.getDoneAt());

        return TodoDTO.fromTodoToTodoDTO(todoRepository.save(todo));
    }

    private static boolean isUpdateOperationAllowed(TodoDTO todoDTO, Todo todo) {
        return Todo.Status.DONE.equals(todo.getStatus()) && !Status.NOT_DONE.equals(todoDTO.status());
    }

    private static boolean isDueTodoItem(Todo todo) {
        return !ObjectUtils.isEmpty(todo.getDueAt()) && LocalDateTime.now().isAfter(todo.getDueAt());
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
                .build()));
    }
}
