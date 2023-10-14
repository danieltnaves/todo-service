package com.danieltnaves.todo.todo.domain;

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
        @JsonSubTypes.Type(value = UpdateItemRequest.UpdateStatusToDoneRequest.class, name = "UPDATE_STATUS_TO_DONE"),
        @JsonSubTypes.Type(value = UpdateItemRequest.UpdateStatusToNotDoneRequest.class, name = "UPDATE_STATUS_TO_NOT_DONE"),
        @JsonSubTypes.Type(value = UpdateItemRequest.UpdateDescription.class, name = "UPDATE_ITEM_DESCRIPTION"),
})
@NoArgsConstructor
public abstract class UpdateItemRequest {

    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class UpdateStatusToDoneRequest extends UpdateItemRequest {

        private Status status;

        public UpdateStatusToDoneRequest() {
            this.status = Status.DONE;
        }

        @Override
        public UpdateItemResponse accept(ChangeStatusVisitor changeStatusVisitor) {
            return changeStatusVisitor.visit(this);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class UpdateStatusToNotDoneRequest extends UpdateItemRequest {

        private Status status;

        public UpdateStatusToNotDoneRequest() {
            this.status = Status.NOT_DONE;
        }

        @Override
        public UpdateItemResponse accept(ChangeStatusVisitor changeStatusVisitor) {
            return changeStatusVisitor.visit(this);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    public static class UpdateDescription extends UpdateItemRequest {

        @NotEmpty
        private String description;

        @Override
        public UpdateItemResponse accept(ChangeStatusVisitor changeStatusVisitor) {
            return changeStatusVisitor.visit(this);
        }
    }

    public abstract UpdateItemResponse accept(ChangeStatusVisitor changeStatusVisitor);
}
