package com.danieltnaves.todo.todo.rules;

import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.api.UpdatePastDueTodoItemWithFutureDateException;
import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Component
public class PastDueUpdateWithFutureDateRule implements TodoItemChangeStateRule {

    public static final String UPDATE_PAST_DUE_WITH_FUTURE_DATE_MESSAGE = "An operation to change the status to PAST_DUE with a future date is not allowed for the Todo id %d";

    @Override
    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (isPastDueUpdateWithFutureDate(todoDTO)) {
            throw new UpdatePastDueTodoItemWithFutureDateException(String.format(UPDATE_PAST_DUE_WITH_FUTURE_DATE_MESSAGE, todo.getId()));
        }
    }

    private boolean isPastDueUpdateWithFutureDate(TodoDTO todoDTO) {
        return TodoDTO.Status.PAST_DUE.equals(todoDTO.status()) && !ObjectUtils.isEmpty(todoDTO.dueAt()) && LocalDateTime.now().isBefore(todoDTO.dueAt());
    }

}
