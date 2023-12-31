package com.danieltnaves.todoservice.todo;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.events.TodoEventPublisherService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoEventPublisherServiceIntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoEventPublisherService todoEventPublisherService;

    @Test
    void testPublishPastDueEvent() {
        Todo todo = Todo.builder()
                .description("This a past due item with the status not yet updated to past due")
                .status(Todo.Status.NOT_DONE)
                .createdAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().minusDays(1))
                .build();
        todoRepository.save(todo);
        todoEventPublisherService.publishUpdatePastDueEvent(todo);
        await().atMost(10, SECONDS).until(() -> Todo.Status.PAST_DUE.equals(todoRepository.findById(todo.getId()).orElseThrow(RuntimeException::new).getStatus()));
    }

}
