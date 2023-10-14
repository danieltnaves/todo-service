package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.Status;
import com.danieltnaves.todo.todo.domain.UpdateItemRequest;
import com.danieltnaves.todo.todo.domain.UpdateItemResponse;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ConcreteChangeStatusVisitor implements ChangeStatusVisitor {
    @Override
    public UpdateItemResponse visit(UpdateItemRequest.UpdateStatusToDoneRequest updateStatusToDoneRequest) {
        return new UpdateItemResponse(Status.DONE, "xxxx", ZonedDateTime.now());
    }

    @Override
    public UpdateItemResponse visit(UpdateItemRequest.UpdateStatusToNotDoneRequest updateStatusToNotDoneRequest) {
        return new UpdateItemResponse(Status.NOT_DONE, "zzzzz", ZonedDateTime.now());
    }

    @Override
    public UpdateItemResponse visit(UpdateItemRequest.UpdateDescription updateDescription) {
        return new UpdateItemResponse(Status.NOT_DONE, "new description", ZonedDateTime.now());
    }
}
