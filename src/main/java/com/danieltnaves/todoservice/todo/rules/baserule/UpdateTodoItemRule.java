package com.danieltnaves.todoservice.todo.rules.baserule;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;

public interface UpdateTodoItemRule {

    void evaluate(TodoDTO todoDTO, Todo todo);

}
