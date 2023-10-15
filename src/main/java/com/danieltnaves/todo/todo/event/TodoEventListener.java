package com.danieltnaves.todo.todo.event;

import com.danieltnaves.todo.todo.TodoService;
import com.danieltnaves.todo.todo.domain.Todo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
public class TodoEventListener {

    private final TodoService todoService;

    public TodoEventListener(TodoService todoService) {
        this.todoService = todoService;
    }

    @EventListener
    public void onUpdatePastDueEvent(UpdatePastDueEvent event) {
        log.info("Event received for past due item {}", event.getId());
        todoService.updateTodoStatusById(event.getId(), Todo.Status.PAST_DUE);
        log.info("Todo item {} updated via event with status PAST_DUE", event.getId());
    }
}
