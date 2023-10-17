package com.danieltnaves.todoservice.todo.rules;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.UpdateDoneTodoItemException;
import org.springframework.stereotype.Component;

@Component
public class UpdateDoneUpdateTodoItemRule extends UpdateTodoItemRuleTemplate {

    private static final String TODO_ITEM_MARKED_AS_DONE_MESSAGE = "The Todo item %d was already marked as DONE. It needs to be updated to NOT_DONE before performing this operation";

    @Override
    protected String getMessage(TodoDTO todoDTO, Todo todo) {
        return String.format(TODO_ITEM_MARKED_AS_DONE_MESSAGE, todo.getId());
    }

    @Override
    protected boolean validate(TodoDTO todoDTO, Todo todo) {
        return Todo.Status.DONE.equals(todo.getStatus()) && !TodoDTO.Status.NOT_DONE.equals(todoDTO.status());
    }

    @Override
    protected void throwException(String message) {
        throw new UpdateDoneTodoItemException(message);
    }

}
