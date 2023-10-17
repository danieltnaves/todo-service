package com.danieltnaves.todoservice.todo.rules;

import static com.danieltnaves.todoservice.todo.api.TodoDTO.Status;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueTodoItemException;
import java.time.LocalDateTime;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

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
        return ObjectUtils.isNotEmpty(todoDTO.dueAt()) && LocalDateTime.now().isAfter(todoDTO.dueAt()) && (!Status.PAST_DUE.equals(todoDTO.status()));
    }
}
