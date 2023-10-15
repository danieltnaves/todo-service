package com.danieltnaves.todo.todo.rules;

import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.api.UpdateDoneTodoItemException;
import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.stereotype.Component;

@Component
public class UpdateDoneUpdateTodoItemRule implements UpdateTodoItemRule {

    private static final String TODO_ITEM_MARKED_AS_DONE_MESSAGE = "The Todo item %d was already marked as DONE. It needs to be updated to NOT_DONE before performing this operation";

    @Override
    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (isDoneTodoItem(todoDTO, todo)) {
            throw new UpdateDoneTodoItemException(String.format(TODO_ITEM_MARKED_AS_DONE_MESSAGE, todo.getId()));
        }
    }

    private boolean isDoneTodoItem(TodoDTO todoDTO, Todo todo) {
        return Todo.Status.DONE.equals(todo.getStatus()) && !TodoDTO.Status.NOT_DONE.equals(todoDTO.status());
    }
}
