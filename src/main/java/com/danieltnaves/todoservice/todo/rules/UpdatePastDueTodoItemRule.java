package com.danieltnaves.todoservice.todo.rules;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueTodoItemException;
import java.time.LocalDateTime;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class UpdatePastDueTodoItemRule implements UpdateTodoItemRule {

    private static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to update the information for the Todo id %d because it's a past due item";

    @Override
    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (isDueTodoItem(todo)) {
            throw new UpdatePastDueTodoItemException(String.format(PAST_DUE_TODO_ITEM_MESSAGE, todo.getId()));
        }
    }

    private boolean isDueTodoItem(Todo todo) {
        return ObjectUtils.isNotEmpty(todo.getDueAt()) && LocalDateTime.now().isAfter(todo.getDueAt());
    }
}
