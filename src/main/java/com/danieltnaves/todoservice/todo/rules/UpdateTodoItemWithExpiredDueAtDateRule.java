package com.danieltnaves.todoservice.todo.rules;

import static com.danieltnaves.todoservice.todo.api.TodoDTO.Status;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueException;
import com.danieltnaves.todoservice.todo.rules.baserule.UpdateTodoItemRuleTemplate;
import java.time.LocalDateTime;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class UpdateTodoItemWithExpiredDueAtDateRule extends UpdateTodoItemRuleTemplate {

    private static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to update a Todo item with a expired date and Status different of PAST_DUE for the item %d";

    @Override
    protected String getMessage(TodoDTO todoDTO, Todo todo) {
        return String.format(PAST_DUE_TODO_ITEM_MESSAGE, todo.getId());
    }

    @Override
    protected boolean validate(TodoDTO todoDTO, Todo todo) {
        return ObjectUtils.isNotEmpty(todoDTO.dueAt()) && LocalDateTime.now().isAfter(todoDTO.dueAt()) && (!Status.PAST_DUE.equals(todoDTO.status()));
    }

    @Override
    protected void throwException(String message) {
        throw new UpdatePastDueException(message);
    }

}
