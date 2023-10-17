package com.danieltnaves.todoservice.todo.rules;

import static com.danieltnaves.todoservice.todo.api.TodoDTO.Status;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UpdateTodoItemStatusToPastDueRule extends UpdateTodoItemRuleTemplate {

    private static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to change a Todo item to a status PAST_DUE with a null or non-expired dueAt date for the item %d";

    @Override
    protected String getMessage(TodoDTO todoDTO, Todo todo) {
        return String.format(PAST_DUE_TODO_ITEM_MESSAGE, todo.getId());
    }

    @Override
    protected boolean validate(TodoDTO todoDTO, Todo todo) {
        return Status.PAST_DUE.equals(todoDTO.status()) && (ObjectUtils.isEmpty(todoDTO.dueAt()) || !LocalDateTime.now().isAfter(todoDTO.dueAt()));
    }

    @Override
    protected void throwException(String message) {
        throw new UpdatePastDueException(message);
    }

}
