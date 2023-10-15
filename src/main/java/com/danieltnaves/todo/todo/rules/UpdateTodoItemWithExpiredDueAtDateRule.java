package com.danieltnaves.todo.todo.rules;

import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.api.UpdatePastDueTodoItemException;
import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

import static com.danieltnaves.todo.todo.api.TodoDTO.*;

@Component
public class UpdateTodoItemWithExpiredDueAtDateRule implements UpdateTodoItemRule {

    private static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to update a Todo item with a expired date and Status different of PAST_DUE for the item %d";

    @Override
    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (isExpiredDueAtDateWithStatusDifferentOfPastDue(todoDTO)) {
            throw new UpdatePastDueTodoItemException(String.format(PAST_DUE_TODO_ITEM_MESSAGE, todo.getId()));
        }
    }

    private boolean isExpiredDueAtDateWithStatusDifferentOfPastDue(TodoDTO todoDTO) {
        return !ObjectUtils.isEmpty(todoDTO.dueAt()) && LocalDateTime.now().isAfter(todoDTO.dueAt()) && (!Status.PAST_DUE.equals(todoDTO.status()));
    }
}
