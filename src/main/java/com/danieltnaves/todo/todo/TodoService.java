package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.*;
import com.danieltnaves.todo.todo.api.TodoDTO.Status;
import com.danieltnaves.todo.todo.domain.Todo;
import com.danieltnaves.todo.todo.event.TodoEventPublisherService;
import com.danieltnaves.todo.todo.rules.TodoItemChangeStateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TodoService {

    public static final String TODO_ITEM_NOT_FOUND_MESSAGE = "The Todo item with the ID %d wasn't found";

    public static final String DESCRIPTION_NOT_PROVIDED_MESSAGE = "The Todo description wasn't provided";

    private final TodoRepository todoRepository;

    private final TodoEventPublisherService todoEventPublisherService;

    private final List<TodoItemChangeStateRule> todoItemChangeStateRules;

    public TodoService(TodoRepository todoRepository, TodoEventPublisherService todoEventPublisherService, List<TodoItemChangeStateRule> todoItemChangeStateRules) {
        this.todoRepository = todoRepository;
        this.todoEventPublisherService = todoEventPublisherService;
        this.todoItemChangeStateRules = todoItemChangeStateRules;
    }

    @Transactional
    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoItemNotFoundException(String.format(TODO_ITEM_NOT_FOUND_MESSAGE, id)));
        evaluateChangeStatusRules(todoDTO, todo);
        changeTodoState(todoDTO, todo);
        return TodoDTO.fromTodoToTodoDTO(todoRepository.save(todo));
    }

    private void changeTodoState(TodoDTO todoDTO, Todo todo) {
        todo.setStatus(!ObjectUtils.isEmpty(todoDTO.status()) ? Todo.Status.fromString(todoDTO.status().name()) : todo.getStatus());
        todo.setDescription(!ObjectUtils.isEmpty(todoDTO.description()) ? todoDTO.description() : todo.getDescription());
        todo.setDoneAt(Status.DONE.equals(todoDTO.status()) ? LocalDateTime.now() : null);
        todo.setDueAt(!ObjectUtils.isEmpty(todoDTO.dueAt()) ? todoDTO.dueAt() : todo.getDueAt());
    }

    private void evaluateChangeStatusRules(TodoDTO todoDTO, Todo todo) {
        todoItemChangeStateRules.forEach(todoItemChangeStateRule -> todoItemChangeStateRule.evaluate(todoDTO, todo));
    }

    public List<TodoDTO> getTodosByFilter(boolean onlyPastDueItems, Integer page, Integer size) {
        if (onlyPastDueItems) {
            return todoRepository.findAllPastDueItems(PageRequest.of(page, size))
                    .stream()
                    .map(this::updatePastDueItemStatus)
                    .map(TodoDTO::fromTodoToTodoDTO)
                    .toList();
        }
        return todoRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(this::updatePastDueItemStatus)
                .map(TodoDTO::fromTodoToTodoDTO)
                .toList();
    }

    private Todo updatePastDueItemStatus(Todo todo) {
        if (isUpdatablePastDueItem(todo)) {
            todo.setStatus(Todo.Status.PAST_DUE);
        }
        return todo;
    }

    public TodoDTO getTodoById(Long id) {
        return TodoDTO.fromTodoToTodoDTO(todoRepository.findById(id)
                .map(this::updatePastDueItemStatus)
                .orElseThrow(() -> new TodoItemNotFoundException(String.format(TODO_ITEM_NOT_FOUND_MESSAGE, id))));
    }

    public TodoDTO addTodo(TodoDTO todoDTO) {
        if (ObjectUtils.isEmpty(todoDTO) || ObjectUtils.isEmpty(todoDTO.description())) {
            throw new InvalidInputException(DESCRIPTION_NOT_PROVIDED_MESSAGE);
        }
        return TodoDTO.fromTodoToTodoDTO(todoRepository.save(Todo.builder()
                .description(todoDTO.description())
                .status(Todo.Status.NOT_DONE)
                .createdAt(LocalDateTime.now())
                .build()));
    }

    @Transactional
    public void updateTodoStatusById(Long id, Todo.Status status) {
        todoRepository.updateTodoStatusById(id, status);
    }

    private boolean isUpdatablePastDueItem(Todo todo) {
        if (!ObjectUtils.isEmpty(todo.getDueAt()) && LocalDateTime.now().isAfter(todo.getDueAt()) && Todo.Status.NOT_DONE.equals(todo.getStatus())) {
            todoEventPublisherService.publishUpdatePastDueEvent(todo);
            return true;
        }
        return false;
    }
}
