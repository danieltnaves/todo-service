package com.danieltnaves.todo.todo.rules;

import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.api.UpdatePastDueTodoItemException;
import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Component
public class UpdatePastDueTodoItemRule implements TodoItemChangeStateRule {

    public static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to update the information for the Todo id %d because it's a past due item";

    @Override
    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (isDueTodoItem(todo)) {
            throw new UpdatePastDueTodoItemException(String.format(PAST_DUE_TODO_ITEM_MESSAGE, todo.getId()));
        }
    }

    private boolean isDueTodoItem(Todo todo) {
        return !ObjectUtils.isEmpty(todo.getDueAt()) && LocalDateTime.now().isAfter(todo.getDueAt());
    }
}
