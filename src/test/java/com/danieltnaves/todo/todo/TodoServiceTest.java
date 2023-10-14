package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.*;
import com.danieltnaves.todo.todo.domain.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

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
                .build();

        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        TodoDTO todoDTO = TodoDTO.builder()
                .description("Go to grocery store")
                .build();
        Todo addedTodo = TodoDTO.fromTodoDTOToTodo(todoService.addTodo(todoDTO));

        assertThat(addedTodo.getDescription(), is(todoDTO.description()));
        assertThat(addedTodo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(addedTodo.getCreatedAt(), is(lessThan(LocalDateTime.now())));
    }

    @Test
    void testTodoAdditionWithMissingDescription() {
        TodoDTO todoDTO = TodoDTO.builder().description(null).build();
        assertThrows(InvalidInputException.class, () -> todoService.addTodo(todoDTO));
    }

    @Test
    void testTodoChangeDescription() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .dueAt(currentDate.plusDays(3))
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        todo.setDescription("Go to the mall");
        when(todoRepository.save(todo)).thenReturn(todo);
        TodoDTO todoDTO = TodoDTO.builder()
                .description("Go to the mall")
                .build();
        Todo changedTodo = TodoDTO.fromTodoDTOToTodo(todoService.updateTodo(1L, todoDTO));

        assertThat(changedTodo.getDescription(), is(todoDTO.description()));
        assertThat(changedTodo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(changedTodo.getDueAt(), is(greaterThan(LocalDateTime.now())));
    }

    @Test
    void testTodoChangeWithNonExistentId() {
        TodoDTO todoDTO = TodoDTO.builder()
                .description("Go to the grocery store")
                .build();
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TodoItemNotFoundException.class, () -> todoService.updateTodo(1L, todoDTO));
    }

    @Test
    void testTodoItemPastDueChange() {
        LocalDateTime dueDate = LocalDateTime.now().minusDays(3);
        Todo todo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.NOT_DONE)
                .createdAt(dueDate)
                .doneAt(dueDate)
                .dueAt(dueDate)
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        TodoDTO todoDTO = TodoDTO.builder().description("Go to the mall").build();

        assertThrows(UpdatePastDueTodoItemException.class, () -> todoService.updateTodo(1L, todoDTO));
    }

    @Test
    void testTodoItemMarkedAsDoneChange() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.DONE)
                .createdAt(currentDate)
                .doneAt(currentDate)
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        TodoDTO todoDTO = TodoDTO.builder().description("Go to the mall").build();

        assertThrows(UpdateDoneTodoItemException.class, () -> todoService.updateTodo(1L, todoDTO));
    }

    @Test
    void testMarkTodoItemAsDone() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .build();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        Todo savedTodo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.DONE)
                .createdAt(currentDate)
                .doneAt(LocalDateTime.now())
                .build();
        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        TodoDTO todoDTO = TodoDTO.builder()
                .status(TodoDTO.Status.DONE)
                .build();
        Todo doneTodo = TodoDTO.fromTodoDTOToTodo(todoService.updateTodo(1L, todoDTO));

        assertThat(doneTodo.getStatus(), is(Todo.Status.DONE));
        assertThat(doneTodo.getDoneAt(), is(greaterThan(currentDate)));
    }

    @Test
    void testMarkTodoDoneItemAsNotDone() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.DONE)
                .createdAt(currentDate)
                .build();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        Todo savedTodo = Todo.builder()
                .id(1L)
                .description("Go to grocery store")
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .build();
        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        TodoDTO todoDTO = TodoDTO.builder()
                .status(TodoDTO.Status.NOT_DONE)
                .build();
        Todo doneTodo = TodoDTO.fromTodoDTOToTodo(todoService.updateTodo(1L, todoDTO));

        assertThat(doneTodo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(doneTodo.getDoneAt(), nullValue());
    }
}


