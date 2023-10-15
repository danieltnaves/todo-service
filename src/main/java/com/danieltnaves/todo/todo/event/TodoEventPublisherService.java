package com.danieltnaves.todo.todo.event;

import com.danieltnaves.todo.todo.domain.Todo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TodoEventPublisherService {

    private final ApplicationEventPublisher eventPublisher;

    public TodoEventPublisherService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishUpdatePastDueEvent(Todo todo) {
        log.info("Publishing an event to update status to PAST_DUE for Todo item id {}", todo.getId());
        eventPublisher.publishEvent(new UpdatePastDueEvent(this, todo.getId()));
    }

}
