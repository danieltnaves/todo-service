package com.danieltnaves.todoservice.todo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import com.danieltnaves.todoservice.todo.domain.Todo;
import com.danieltnaves.todoservice.todo.errors.InvalidInputException;
import com.danieltnaves.todoservice.todo.errors.TodoItemNotFoundException;
import com.danieltnaves.todoservice.todo.errors.UpdateDoneTodoItemException;
import com.danieltnaves.todoservice.todo.errors.UpdatePastDueException;
import com.danieltnaves.todoservice.todo.events.TodoEventPublisherService;
import com.danieltnaves.todoservice.todo.rules.PastDueUpdateWithFutureDateRuleUpdate;
import com.danieltnaves.todoservice.todo.rules.UpdateDoneUpdateTodoItemRule;
import com.danieltnaves.todoservice.todo.rules.UpdatePastDueTodoItemRule;
import com.danieltnaves.todoservice.todo.rules.baserule.UpdateTodoItemRule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        List<UpdateTodoItemRule> updateTodoItemRules = List.of(new PastDueUpdateWithFutureDateRuleUpdate(), new UpdatePastDueTodoItemRule(), new UpdateDoneUpdateTodoItemRule());
        todoService = new TodoService(todoRepository, todoEventPublisherService, updateTodoItemRules);
    }

    @Test
    void testAddTodoItem() {
        when(todoRepository.save(any(Todo.class))).thenReturn(getNewTodoItem(LocalDateTime.now()));
        Todo addedTodo = TodoDTO.fromTodoDTOToTodo(todoService.addTodoItem(getNewTodoDTO()));
        assertThat(addedTodo.getDescription(), is(GO_TO_GROCERY_STORE));
        assertThat(addedTodo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(addedTodo.getCreatedAt(), is(lessThan(LocalDateTime.now())));
    }

    @Test
    void testTodoAdditionWithMissingDescription() {
        assertThrows(InvalidInputException.class, () -> todoService.addTodoItem(getTodoDTOWithoutDescription()));
    }

    @Test
    void testTodoChangeDescription() {
        Todo todo = getNotDoneTodoItem();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        todo.setDescription(GO_TO_THE_MALL);
        when(todoRepository.save(todo)).thenReturn(todo);
        TodoDTO todoDTO = getChangedTodoDTO();
        Todo changedTodo = TodoDTO.fromTodoDTOToTodo(todoService.updateTodoItem(1L, todoDTO));
        assertThat(changedTodo.getDescription(), is(GO_TO_THE_MALL));
        assertThat(changedTodo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(changedTodo.getDueAt(), is(greaterThan(LocalDateTime.now())));
    }

    @Test
    void testTodoChangeWithNonExistentId() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TodoItemNotFoundException.class, () -> todoService.updateTodoItem(1L, getNewTodoDTO()));
    }

    @Test
    void testTodoItemPastDueChange() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(getPastDueTodo(LocalDateTime.now().minusDays(3))));
        assertThrows(UpdatePastDueException.class, () -> todoService.updateTodoItem(1L, getChangedTodoDTO()));
    }

    @Test
    void testTodoItemMarkedAsDoneChange() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(getDoneTodo(LocalDateTime.now())));
        assertThrows(UpdateDoneTodoItemException.class, () -> todoService.updateTodoItem(1L, getChangedTodoDTO()));
    }

    @Test
    void testMarkTodoItemAsDone() {
        LocalDateTime currentDate = LocalDateTime.now();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(getNotDoneTodo(currentDate)));
        when(todoRepository.save(any(Todo.class))).thenReturn(getTodoMarkedAsDone(currentDate));
        Todo doneTodo = TodoDTO.fromTodoDTOToTodo(todoService.updateTodoItem(1L, getUpdateTodoToDoneDTO()));
        assertThat(doneTodo.getStatus(), is(Todo.Status.DONE));
        assertThat(doneTodo.getDoneAt(), is(greaterThan(currentDate)));
    }

    @Test
    void testMarkTodoDoneItemAsNotDone() {
        LocalDateTime currentDate = LocalDateTime.now();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(getDoneTodo(currentDate)));
        when(todoRepository.save(any(Todo.class))).thenReturn(getNewTodoItem(currentDate));
        Todo doneTodo = TodoDTO.fromTodoDTOToTodo(todoService.updateTodoItem(1L, getUpdateNotDoneTodoDTO()));
        assertThat(doneTodo.getStatus(), is(Todo.Status.NOT_DONE));
        assertThat(doneTodo.getDoneAt(), nullValue());
    }

    @Test
    void testGetTodoItem() {
        LocalDateTime currentDate = LocalDateTime.now();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(getNewTodoItem(currentDate)));
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
        Page<Todo> todoPage = new PageImpl<>(List.of(getDoneTodo(LocalDateTime.now().minusDays(2)), getNotDoneTodo(LocalDateTime.now())), PageRequest.of(0, 5), 2);
        when(todoRepository.findAll(PageRequest.of(0, 5))).thenReturn(todoPage);
        assertThat(todoService.getTodosByFilter(false, 0, 5), hasSize(2));
    }

    @Test
    void testGetAllItemsByStatus() {
        Page<Todo> todoPage = new PageImpl<>(List.of(getNotDoneTodo(LocalDateTime.now()), getNotDoneTodo(LocalDateTime.now())), PageRequest.of(0, 5), 2);
        when(todoRepository.findAllByStatus(Todo.Status.NOT_DONE, PageRequest.of(0, 5))).thenReturn(todoPage);
        assertThat(todoService.getTodosByFilter(true, 0, 5), hasSize(2));
    }

    private static TodoDTO getNewTodoDTO() {
        return TodoDTO.builder()
                .description(GO_TO_GROCERY_STORE)
                .build();
    }

    private static Todo getNewTodoItem(LocalDateTime currentDate) {
        return Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .build();
    }

    private static TodoDTO getTodoDTOWithoutDescription() {
        return TodoDTO.builder().description(null).build();
    }

    private static TodoDTO getChangedTodoDTO() {
        return TodoDTO.builder()
                .description(GO_TO_THE_MALL)
                .build();
    }

    private static Todo getNotDoneTodoItem() {
        return Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusDays(3))
                .build();
    }

    private static Todo getPastDueTodo(LocalDateTime dueDate) {
        return Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(dueDate)
                .doneAt(dueDate)
                .dueAt(dueDate)
                .build();
    }

    private static Todo getDoneTodo(LocalDateTime currentDate) {
        return Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.DONE)
                .createdAt(currentDate)
                .doneAt(currentDate)
                .build();
    }

    private static TodoDTO getUpdateTodoToDoneDTO() {
        return TodoDTO.builder()
                .status(TodoDTO.Status.DONE)
                .build();
    }

    private static Todo getTodoMarkedAsDone(LocalDateTime currentDate) {
        return Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.DONE)
                .createdAt(currentDate)
                .doneAt(LocalDateTime.now())
                .build();
    }

    private static Todo getNotDoneTodo(LocalDateTime currentDate) {
        return Todo.builder()
                .id(1L)
                .description(GO_TO_GROCERY_STORE)
                .status(Todo.Status.NOT_DONE)
                .createdAt(currentDate)
                .build();
    }

    private static TodoDTO getUpdateNotDoneTodoDTO() {
        return TodoDTO.builder()
                .status(TodoDTO.Status.NOT_DONE)
                .build();
    }

}


