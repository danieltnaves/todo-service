package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.UpdateTodoRequest;
import com.danieltnaves.todo.todo.api.UpdateTodoResponse;

public interface ChangeStatusVisitor {

    UpdateTodoResponse visit(UpdateTodoRequest.UpdateStatusToDoneRequest updateStatusToDoneRequest);

    UpdateTodoResponse visit(UpdateTodoRequest.UpdateStatusToNotDoneRequest updateStatusToNotDoneRequest);

    UpdateTodoResponse visit(UpdateTodoRequest.UpdateDescription updateDescription);
}
