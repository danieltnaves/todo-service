package com.danieltnaves.todo.unit.todo;

import com.danieltnaves.todo.todo.TodoRepository;
import com.danieltnaves.todo.todo.TodoService;
import com.danieltnaves.todo.todo.api.*;
import com.danieltnaves.todo.todo.domain.Todo;
import com.danieltnaves.todo.todo.event.TodoEventPublisherService;
import com.danieltnaves.todo.todo.rules.PastDueUpdateWithFutureDateRuleUpdate;
import com.danieltnaves.todo.todo.rules.UpdateDoneUpdateTodoItemRule;
import com.danieltnaves.todo.todo.rules.UpdatePastDueTodoItemRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    public static final String GO_TO_GROCERY_STORE = "Go to grocery store";

    public static final String GO_TO_THE_MALL = "Go to the mall";

    TodoRepository todoRepository;

    TodoService todoService;

    TodoEventPublisherService todoEventPublisherService;

    @BeforeEach
    void setUp() {
        todoRepository = mock(TodoRepository.class);
        todoEventPublisherService = mock(TodoEventPublisherService.class);
        todoService = new TodoService(todoRepository, todoEventPublisherService, List.of(new PastDueUpdateWithFutureDateRuleUpdate(), new UpdatePastDueTodoItemRule(), new UpdateDoneUpdateTodoItemRule()));
    }

    @Test
    void testTodoAddition() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .build();

        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        TodoDTO todoDTO = TodoDTO.builder()
                .description(GO_TO_GROCERY_STORE)
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
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .dueAt(currentDate.plusDays(3))
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        todo.setDescription(GO_TO_THE_MALL);
        when(todoRepository.save(todo)).thenReturn(todo);
        TodoDTO todoDTO = TodoDTO.builder()
                .description(GO_TO_THE_MALL)
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
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(dueDate)
                .doneAt(dueDate)
                .dueAt(dueDate)
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        TodoDTO todoDTO = TodoDTO.builder().description(GO_TO_THE_MALL).build();

        assertThrows(UpdatePastDueTodoItemException.class, () -> todoService.updateTodo(1L, todoDTO));
    }

    @Test
    void testTodoItemMarkedAsDoneChange() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.DONE)
                .createdAt(currentDate)
                .doneAt(currentDate)
                .build();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        TodoDTO todoDTO = TodoDTO.builder().description(GO_TO_THE_MALL).build();

        assertThrows(UpdateDoneTodoItemException.class, () -> todoService.updateTodo(1L, todoDTO));
    }

    @Test
    void testMarkTodoItemAsDone() {
        LocalDateTime currentDate = LocalDateTime.now();
        Todo todo = Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .build();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        Todo savedTodo = Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
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
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.DONE)
                .createdAt(currentDate)
                .build();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        Todo savedTodo = Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
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

    @Test
    void testGetTodoItem() {
        LocalDateTime currentDate = LocalDateTime.now();

        Todo savedTodo = Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .build();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(savedTodo));

        Todo todo = TodoDTO.fromTodoDTOToTodo(todoService.getTodoById(1L));
        assertThat(todo.getId(), is(1L));
        assertThat(todo.getDescription(), is(GO_TO_GROCERY_STORE));
        assertThat(todo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(todo.getCreatedAt(), is(currentDate));
    }

    @Test
    void testGetNonExistentTodoItem() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TodoItemNotFoundException.class, () -> todoService.getTodoById(1L));
    }

    @Test
    void testGetAllItems() {
        Todo doneTodo = Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.DONE)
                .createdAt(LocalDateTime.now().minusDays(2))
                .doneAt(LocalDateTime.now().minusDays(1))
                .build();
        Todo notNodeTodo = Todo.builder()
                .id(2L)
                .description(GO_TO_THE_MALL)
                .status(Todo.Status.NOT_DONE)
                .createdAt(LocalDateTime.now())
                .build();

        Page<Todo> todoPage = new PageImpl<>(List.of(doneTodo, notNodeTodo), PageRequest.of(0, 5), 2);
        when(todoRepository.findAll(PageRequest.of(0, 5))).thenReturn(todoPage);

        assertThat(todoService.getTodosByFilter(false, 0, 5), hasSize(2));
    }

    @Test
    void testGetAllItemsByStatus() {
        Todo notNodeTodo = Todo.builder()
                .id(2L)
                .description(GO_TO_THE_MALL)
                .status(Todo.Status.PAST_DUE)
                .createdAt(LocalDateTime.now())
                .build();
        Todo notNodeTodo2 = Todo.builder()
                .id(3L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.PAST_DUE)
                .createdAt(LocalDateTime.now())
                .build();

        Page<Todo> todoPage = new PageImpl<>(List.of(notNodeTodo, notNodeTodo2), PageRequest.of(0, 5), 2);
        when(todoRepository.findAllPastDueItems(PageRequest.of(0, 5))).thenReturn(todoPage);

        assertThat(todoService.getTodosByFilter(true, 0, 5), hasSize(2));
    }
}


