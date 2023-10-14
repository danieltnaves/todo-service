package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.Status;
import com.danieltnaves.todo.todo.api.UpdateTodoRequest;
import com.danieltnaves.todo.todo.api.UpdateTodoResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConcreteChangeStatusVisitor implements ChangeStatusVisitor {
    @Override
    public UpdateTodoResponse visit(UpdateTodoRequest.UpdateStatusToDoneRequest updateStatusToDoneRequest) {
        return new UpdateTodoResponse(Status.DONE, "xxxx", LocalDateTime.now());
    }

    @Override
    public UpdateTodoResponse visit(UpdateTodoRequest.UpdateStatusToNotDoneRequest updateStatusToNotDoneRequest) {
        return new UpdateTodoResponse(Status.NOT_DONE, "zzzzz", LocalDateTime.now());
    }

    @Override
    public UpdateTodoResponse visit(UpdateTodoRequest.UpdateDescription updateDescription) {
        return new UpdateTodoResponse(Status.NOT_DONE, "new description", LocalDateTime.now());
    }
}
