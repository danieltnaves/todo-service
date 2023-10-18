package com.danieltnaves.todoservice.todo.rules;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueException;
import com.danieltnaves.todoservice.todo.rules.baserule.UpdateTodoItemRuleTemplate;
import java.time.LocalDateTime;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class UpdatePastDueTodoItemRule extends UpdateTodoItemRuleTemplate {

    private static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to update the information for the Todo id %d because it's a past due item";

    @Override
    protected String getMessage(TodoDTO todoDTO, Todo todo) {
        return String.format(PAST_DUE_TODO_ITEM_MESSAGE, todo.getId());
    }

    @Override
    protected boolean validate(TodoDTO todoDTO, Todo todo) {
        return ObjectUtils.isNotEmpty(todo.getDueAt()) && LocalDateTime.now().isAfter(todo.getDueAt());
    }

    @Override
    protected void throwException(String message) {
        throw new UpdatePastDueException(message);
    }

}
