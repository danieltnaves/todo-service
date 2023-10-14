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
    public ResponseEntity<TodoDTO> addItem(@RequestBody TodoDTO todoDTO) {
        return ResponseEntity.created(URI.create("/todo/1")).body(todoService.addTodo(todoDTO));
    }

    @PatchMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<TodoDTO> getItems(@PathVariable Long id, @Valid @RequestBody TodoDTO todoDTO) {
        return ResponseEntity.ok().body(todoService.updateTodo(id, todoDTO));
    }

    @GetMapping(path = "todo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<TodoDTO> getItems(@RequestParam(name = "status", required = false) TodoDTO.Status status) {
        return ResponseEntity.ok().body(todoService.getTodosByFilter(status));
    }

    @GetMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<TodoDTO> getItems(@PathVariable Long id) {
        return ResponseEntity.ok().body(todoService.getTodoById(id));
    }
}
