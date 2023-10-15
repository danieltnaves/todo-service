package com.danieltnaves.todo.todo.event;

import org.springframework.context.ApplicationEvent;

public class UpdatePastDueEvent extends ApplicationEvent {

    private Long id;

    public UpdatePastDueEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
