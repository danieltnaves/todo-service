package com.danieltnaves.todo.todo.api;

import java.util.List;

public record GetTodosResponse(List<TodoItemResponse> todos) {
}
