package com.danieltnaves.todo.todo.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdatePastDueEvent extends ApplicationEvent {

    private final Long id;

    public UpdatePastDueEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }

}
