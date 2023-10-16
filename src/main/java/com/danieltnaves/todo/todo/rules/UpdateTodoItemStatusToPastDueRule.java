package com.danieltnaves.todo.todo.rules;

import static com.danieltnaves.todo.todo.api.TodoDTO.Status;

import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.api.UpdatePastDueTodoItemException;
import com.danieltnaves.todo.todo.domain.Todo;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UpdateTodoItemStatusToPastDueRule implements UpdateTodoItemRule {

    private static final String PAST_DUE_TODO_ITEM_MESSAGE = "It's not allowed to change a Todo item to a status PAST_DUE with a null or non-expired dueAt date for the item %d";

    @Override
    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (isPastDueStatusWithoutValidDueAtDate(todoDTO)) {
            throw new UpdatePastDueTodoItemException(String.format(PAST_DUE_TODO_ITEM_MESSAGE, todo.getId()));
        }
    }

    private boolean isPastDueStatusWithoutValidDueAtDate(TodoDTO todoDTO) {
        return Status.PAST_DUE.equals(todoDTO.status()) && (ObjectUtils.isEmpty(todoDTO.dueAt()) || !LocalDateTime.now().isAfter(todoDTO.dueAt()));
    }
}
