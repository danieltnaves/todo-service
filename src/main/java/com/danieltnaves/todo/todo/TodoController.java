package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.api.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;

@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping(path = "todo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public ResponseEntity<AddTodoResponse> addItem(@RequestBody AddTodoRequest addTodoRequest) {
        return ResponseEntity.created(URI.create("/todo/1")).body(todoService.addTodo(addTodoRequest));
    }

    @PatchMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<UpdateTodoResponse> getItems(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest updateTodoRequest) {
        return ResponseEntity.ok().body(todoService.updateTodo(id, updateTodoRequest));
    }

    @GetMapping(path = "todo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<GetTodosResponse> getItems(@RequestParam(name = "status", required = false) Status status) {
        return ResponseEntity.ok().body(todoService.getTodosByFilter(status));
    }

    @GetMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<GetTodosResponse> getItems(@PathVariable Long id) {
        return ResponseEntity.ok().body(todoService.getTodoById(id));
    }
}
