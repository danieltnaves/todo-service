package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.springframework.http.HttpStatus.*;

@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping(path = "todo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public ResponseEntity<AddItemResponse> addItem(@RequestBody AddItemRequest addItemRequest) {
        return ResponseEntity.created(URI.create("/todo/1")).body(new AddItemResponse(Status.NOT_DONE, ZonedDateTime.now()));
    }

    @PatchMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<UpdateItemResponse> getItems(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest updateItemRequest) {
        return ResponseEntity.ok().body(todoService.updateItem(id, updateItemRequest));
    }

    @GetMapping(path = "todo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<GetItemsByFilterResponse> getItems(@RequestParam(name = "status", required = false) Status status) {
        return ResponseEntity.ok().body(todoService.getItemsByFilter(status));
    }

    @GetMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<GetItemsByFilterResponse> getItems(@PathVariable Long id) {
        return ResponseEntity.ok().body(todoService.getItemById(id));
    }
}
