package com.danieltnaves.todoservice.todo.rules;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueTodoItemWithFutureDateException;
import com.danieltnaves.todoservice.todo.rules.baserule.UpdateTodoItemRuleTemplate;
import java.time.LocalDateTime;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class PastDueUpdateWithFutureDateRuleUpdate extends UpdateTodoItemRuleTemplate {

    private static final String UPDATE_PAST_DUE_WITH_FUTURE_DATE_MESSAGE = "An operation to change the status to PAST_DUE with a future date is not allowed for the Todo id %d";

    @Override
    protected String getMessage(TodoDTO todoDTO, Todo todo) {
        return String.format(UPDATE_PAST_DUE_WITH_FUTURE_DATE_MESSAGE, todo.getId());
    }

    @Override
    protected boolean validate(TodoDTO todoDTO, Todo todo) {
        return TodoDTO.Status.PAST_DUE.equals(todoDTO.status()) && ObjectUtils.isNotEmpty(todoDTO.dueAt()) && LocalDateTime.now().isBefore(todoDTO.dueAt());
    }

    @Override
    protected void throwException(String message) {
        throw new UpdatePastDueTodoItemWithFutureDateException(message);
    }

}
