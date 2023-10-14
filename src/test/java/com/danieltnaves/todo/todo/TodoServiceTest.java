package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.InvalidInputException;
import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.domain.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;

class TodoServiceTest {

    TodoRepository todoRepository;

    TodoService todoService;

    @BeforeEach
    void setUp() {
        todoRepository = mock(TodoRepository.class);
        todoService = new TodoService(todoRepository);
    }
    @Test
    void testTodoAddition() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .updatedAt(currentDate)
                .dueAt(currentDate.plusDays(3))
                .build();

        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        TodoDTO todoDTO = TodoDTO.builder().description("Go to grocery store").build();
        Todo addedTodo = TodoDTO.fromTodoDTOToTodo(todoService.addTodo(todoDTO));

        assertThat(addedTodo.getDescription(), is(todoDTO.description()));
        assertThat(addedTodo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(addedTodo.getCreatedAt(), is(lessThan(LocalDateTime.now())));
        assertThat(addedTodo.getUpdatedAt(), is(lessThan(LocalDateTime.now())));
        assertThat(addedTodo.getDueAt(), is(greaterThan(LocalDateTime.now())));
    }

    @Test
    void testTodoAdditionWithMissingDescription() {
        TodoDTO todoDTO = TodoDTO.builder().description(null).build();
        assertThrows(InvalidInputException.class, () -> todoService.addTodo(todoDTO));
    }
}


