package com.danieltnaves.todo.todo;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.danieltnaves.todo.todo.api.TodoDTO;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping(path = "todo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public ResponseEntity<TodoDTO> addItem(@RequestBody TodoDTO todoDTO) {
        TodoDTO createdTodo = todoService.addTodo(todoDTO);
        return ResponseEntity.created(URI.create(String.format("/todo/%s", createdTodo.id()))).body(createdTodo);
    }

    @PatchMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<TodoDTO> getItems(@PathVariable Long id, @Valid @RequestBody TodoDTO todoDTO) {
        return ResponseEntity.ok().body(todoService.updateTodo(id, todoDTO));
    }

    @GetMapping(path = "todo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<List<TodoDTO>> getItems(@RequestParam(name = "onlyNotDone", required = false) boolean onlyPastDueItems,
                                                  @RequestParam(name = "page") Integer page,
                                                  @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok().body(todoService.getTodosByFilter(onlyPastDueItems, page, size));
    }

    @GetMapping(path = "todo/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<TodoDTO> getItem(@PathVariable Long id) {
        return ResponseEntity.ok().body(todoService.getTodoById(id));
    }
}
