package com.danieltnaves.todo.todo.rules;

import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.domain.Todo;

public interface TodoItemChangeStateRule {

    void evaluate(TodoDTO todoDTO, Todo todo);

}
