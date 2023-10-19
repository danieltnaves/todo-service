package com.danieltnaves.todoservice.todo;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@OpenAPIDefinition(info = @Info(
        title = "Todo Service API",
        description = """
                This REST API provides features to add, update, and list Todo items.
                This OpenApi documentation also provides a series of request and response
                examples. Switch between dropdown values to change the payloads that can be
                sent for the PATCH operation.
                """,
        contact = @Contact(name = "Daniel Naves", url = "https://github.com/danieltnaves", email = "daniel.naves@outlook.com")
))
@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @Operation(summary = "Add a new Todo item", description = """
            Add a new Todo item with the initial status NOT_DONE and automatically populate the createdAt.
            Only the description is required to create a new Todo item. The location to the new created
            item is also returned on the response header location.
            """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(examples = {
                    @ExampleObject(value = """
                            {
                              "description": "Go to the mall"
                            }
                            """)
            })
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation and a new Todo item was created", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            {
                              "id": 1,
                              "description": "Go to the mall",
                              "status": "NOT_DONE",
                              "createdAt": "2023-10-17T20:54:42.862674"
                            }
                            """)
                    })
            }),
            @ApiResponse(responseCode = "400", description = "Bad request due wrong information sent from the client", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "message": "The Todo description wasn't provided",
                              "description": "uri=/todo-service/todo"
                            }
                            """)
                    })
            })
    })
    @PostMapping(path = "todo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public ResponseEntity<TodoDTO> addItem(@RequestBody TodoDTO todoDTO) {
        TodoDTO createdTodo = todoService.addTodoItem(todoDTO);
        return ResponseEntity.created(URI.create(String.format("/todo/%s", createdTodo.id()))).body(createdTodo);
    }

    @Operation(summary = "Update an existing item", description = """
            This endpoint updates a given item by id sent via path param. There are some operations that are not allowed:
            1) Update an items already done. It needs to be changed to NOT_DONE before updating any other information like description for example.
            2) Update an PAST_DUE item.
            3) Update the status to PAST_DUE with a non-expired/future date.
            4) Update a Todo item with a expired date and a status different of PAST_DUE
            """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(examples = {
                    @ExampleObject(value = """
                            {
                              "description": "Go to the mall changed"
                            }
                            """,
                            name = "Change the description of a given item"
                    ),
                    @ExampleObject(value = """
                            {
                              "status": "DONE"
                            }
                            """,
                            name = "Mark an item as done"
                    ),
                    @ExampleObject(value = """
                            {
                              "description": "Go to the mall changed",
                              "dueAt": "2023-10-25T21:30:29.88179"
                            }
                            """,
                            name = "Change the description and the dueAt date"
                    ),
                    @ExampleObject(value = """
                            {
                              "status" : "PAST_DUE",
                              "dueAt": "2023-10-11T21:30:29.88179"
                            }
                            """,
                            name = "Change an item to PAST_DUE"
                    ),
                    @ExampleObject(value = """
                            {
                              "status": "NOT_DONE"
                            }
                            """,
                            name = "Change an item from DONE to NOT_DONE"
                    )
            })
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The item was updated with success", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            {
                              "id": 1,
                              "description": "Go to the mall",
                              "status": "DONE",
                              "createdAt": "2023-10-17T21:54:11.322878",
                              "doneAt": "2023-10-17T21:54:35.684929"
                            }
                            """)
                    })
            }),
            @ApiResponse(responseCode = "400", description = "Bad request due wrong information sent from the client", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "message": "The Todo item 1 was already marked as DONE. It needs to be updated to NOT_DONE before performing this operation",
                              "description": "uri=/todo-service/todo/1"
                            }
                            """)
                    })
            }),
            @ApiResponse(responseCode = "404", description = "The Todo item ID wasn't found", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            {
                              "httpStatus": "NOT_FOUND",
                              "message": "The Todo item with the ID 1312 wasn't found",
                              "description": "uri=/todo-service/todo/1312"
                            }
                            """)
                    })
            })
    })
    @PatchMapping(path = "todo/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<TodoDTO> updateItem(@PathVariable Long id, @Valid @RequestBody TodoDTO todoDTO) {
        return ResponseEntity.ok().body(todoService.updateTodoItem(id, todoDTO));
    }

    @Operation(summary = "Retrieve a list of Todo items", description = """
            This endpoint retrieves a list of Todo items using pagination. Basically, there are two options to retrieve the Todo items:
            1) With the flag onlyNotDone = true. When this flag is set as true only the items with status NOT_DONE will be retrieved.
            2) With the flag onlyNotDone = false. When this flags is set as false this endpoint retrieves all Todo items.
            
            The default value is false and onlyNotDone is non required.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieve a list of Todo items", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            [
                              {
                                "id": 1,
                                "description": "Go to the mall",
                                "status": "NOT_DONE",
                                "createdAt": "2023-10-17T21:11:10.73867"
                              },
                              {
                                "id": 2,
                                "description": "Finish to read my favorite book",
                                "status": "DONE",
                                "createdAt": "2023-10-17T21:11:30.979739",
                                "doneAt": "2023-10-17T21:12:11.267001"
                              }
                            ]
                            """)
                    })
            })
    })
    @GetMapping(path = "todo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<List<TodoDTO>> updateItem(@RequestParam(name = "onlyNotDone", required = false) boolean onlyPastDueItems,
                                                    @RequestParam(name = "page") Integer page,
                                                    @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok().body(todoService.getTodosByFilter(onlyPastDueItems, page, size));
    }

    @Operation(summary = "Retrieve an existing item", description = """
            This endpoint retrieves and existing Todo item using URL path param. In case the ID doesn't exist, it returns 404.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the Todo item information based on the ID sent via parameter", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            {
                              "id": 1,
                              "description": "Go to the mall",
                              "status": "DONE",
                              "createdAt": "2023-10-17T21:54:11.322878",
                              "doneAt": "2023-10-17T21:54:35.684929"
                            }
                            """)
                    })
            }),
            @ApiResponse(responseCode = "404", description = "The Todo item ID wasn't found", content = {
                    @Content(mediaType = "application/json", examples = { @ExampleObject(value = """
                            {
                              "httpStatus": "NOT_FOUND",
                              "message": "The Todo item with the ID 1312 wasn't found",
                              "description": "uri=/todo-service/todo/1312"
                            }
                            """)
                    })
            })
    })
    @GetMapping(path = "todo/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity<TodoDTO> getItem(@PathVariable Long id) {
        return ResponseEntity.ok().body(todoService.getTodoById(id));
    }
}
