package com.danieltnaves.todo.todo.api;

import com.danieltnaves.todo.todo.ChangeStatusVisitor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "action", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateTodoRequest.UpdateStatusToDoneRequest.class, name = "UPDATE_STATUS_TO_DONE"),
        @JsonSubTypes.Type(value = UpdateTodoRequest.UpdateStatusToNotDoneRequest.class, name = "UPDATE_STATUS_TO_NOT_DONE"),
        @JsonSubTypes.Type(value = UpdateTodoRequest.UpdateDescription.class, name = "UPDATE_ITEM_DESCRIPTION"),
})
@NoArgsConstructor
public abstract class UpdateTodoRequest {

    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class UpdateStatusToDoneRequest extends UpdateTodoRequest {

        private Status status;

        public UpdateStatusToDoneRequest() {
            this.status = Status.DONE;
        }

        @Override
        public UpdateTodoResponse accept(ChangeStatusVisitor changeStatusVisitor) {
            return changeStatusVisitor.visit(this);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class UpdateStatusToNotDoneRequest extends UpdateTodoRequest {

        private Status status;

        public UpdateStatusToNotDoneRequest() {
            this.status = Status.NOT_DONE;
        }

        @Override
        public UpdateTodoResponse accept(ChangeStatusVisitor changeStatusVisitor) {
            return changeStatusVisitor.visit(this);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class UpdateDescription extends UpdateTodoRequest {

        @NotEmpty
        private String description;

        @Override
        public UpdateTodoResponse accept(ChangeStatusVisitor changeStatusVisitor) {
            return changeStatusVisitor.visit(this);
        }
    }

    public abstract UpdateTodoResponse accept(ChangeStatusVisitor changeStatusVisitor);
}
