package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.GetItemsByFilterResponse;
import com.danieltnaves.todo.todo.domain.Status;
import com.danieltnaves.todo.todo.domain.UpdateItemRequest;
import com.danieltnaves.todo.todo.domain.UpdateItemResponse;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private final ChangeStatusVisitor changeStatusVisitor;

    public TodoService(ChangeStatusVisitor changeStatusVisitor) {
        this.changeStatusVisitor = changeStatusVisitor;
    }

    public UpdateItemResponse updateItem(Long id, UpdateItemRequest updateItemRequest) {
        return updateItemRequest.accept(changeStatusVisitor);
    }

    public GetItemsByFilterResponse getItemsByFilter(Status status) {
        return null;
    }

    public GetItemsByFilterResponse getItemById(Long id) {
        return null;
    }
}
