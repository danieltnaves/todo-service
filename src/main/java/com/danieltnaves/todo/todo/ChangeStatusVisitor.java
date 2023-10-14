package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.UpdateItemRequest;
import com.danieltnaves.todo.todo.domain.UpdateItemResponse;

public interface ChangeStatusVisitor {

    UpdateItemResponse visit(UpdateItemRequest.UpdateStatusToDoneRequest updateStatusToDoneRequest);

    UpdateItemResponse visit(UpdateItemRequest.UpdateStatusToNotDoneRequest updateStatusToNotDoneRequest);

    UpdateItemResponse visit(UpdateItemRequest.UpdateDescription updateDescription);
}
