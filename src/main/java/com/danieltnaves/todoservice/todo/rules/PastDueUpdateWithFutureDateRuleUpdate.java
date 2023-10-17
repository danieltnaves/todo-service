package com.danieltnaves.todoservice.todo.rules;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueTodoItemWithFutureDateException;
import java.time.LocalDateTime;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class PastDueUpdateWithFutureDateRuleUpdate implements UpdateTodoItemRule {

    private static final String UPDATE_PAST_DUE_WITH_FUTURE_DATE_MESSAGE = "An operation to change the status to PAST_DUE with a future date is not allowed for the Todo id %d";

    @Override
    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (isPastDueUpdateWithFutureDate(todoDTO)) {
            throw new UpdatePastDueTodoItemWithFutureDateException(String.format(UPDATE_PAST_DUE_WITH_FUTURE_DATE_MESSAGE, todo.getId()));
        }
    }

    private boolean isPastDueUpdateWithFutureDate(TodoDTO todoDTO) {
        return TodoDTO.Status.PAST_DUE.equals(todoDTO.status()) && ObjectUtils.isNotEmpty(todoDTO.dueAt()) && LocalDateTime.now().isBefore(todoDTO.dueAt());
    }

}
