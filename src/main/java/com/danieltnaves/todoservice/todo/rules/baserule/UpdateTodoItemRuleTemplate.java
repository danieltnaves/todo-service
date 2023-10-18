package com.danieltnaves.todoservice.todo.rules.baserule;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;

public abstract class UpdateTodoItemRuleTemplate implements UpdateTodoItemRule {

    public void evaluate(TodoDTO todoDTO, Todo todo) {
        if (validate(todoDTO, todo)) {
            throwException(getMessage(todoDTO, todo));
        }
    }

    protected abstract String getMessage(TodoDTO todoDTO, Todo todo);

    protected abstract boolean validate(TodoDTO todoDTO, Todo todo);

    protected abstract void throwException(String message);

}
