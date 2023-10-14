package com.danieltnaves.todo.todo.domain;

import java.util.List;

public record GetItemsByFilterResponse(List<ItemResponse> items) {
}
