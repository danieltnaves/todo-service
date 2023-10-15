package com.danieltnaves.todo.integration;

import com.danieltnaves.todo.todo.TodoRepository;
import com.danieltnaves.todo.todo.api.TodoDTO;
import com.danieltnaves.todo.todo.api.TodoDTO.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoApiIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private TodoRepository todoRepository;

	@BeforeEach
	void clean() {
		todoRepository.deleteAll();
	}

	@Test
	void testAddItem() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();

		assertThat(todo, is(notNullValue()));
		assertThat(response.getStatusCode(), is(CREATED));
		assertThat(todo.description(), is("New Todo Item"));
		assertThat(todo.status(), is(Status.NOT_DONE));
		assertThat(todo.createdAt(), lessThan(LocalDateTime.now()));
	}

	@Test
	void testChangeDescription() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> responseCreatedTodo = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = responseCreatedTodo.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO todoWithNewDescription = TodoDTO.builder()
				.description("New Todo Item description")
				.build();
		ResponseEntity<TodoDTO> response = restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(todoWithNewDescription), TodoDTO.class);
		TodoDTO changedTodo = response.getBody();

		assertThat(changedTodo, is(notNullValue()));
		assertThat(response.getStatusCode(), is(OK));
		assertThat(changedTodo.description(), is("New Todo Item description"));
	}

	@Test
	void testMarkAnItemAsDone() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> responseCreatedTodo = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = responseCreatedTodo.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO todoMarkedAsDone = TodoDTO.builder()
				.status(Status.DONE)
				.build();
		ResponseEntity<TodoDTO> response = restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(todoMarkedAsDone), TodoDTO.class);
		TodoDTO doneTodo = response.getBody();

		assertThat(doneTodo, is(notNullValue()));
		assertThat(response.getStatusCode(), is(OK));
		assertThat(doneTodo.status(), is(Status.DONE));
	}

	@Test
	void testGetAllItems() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		IntStream.rangeClosed(1, 5).forEach(i -> restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class));
		ResponseEntity<TodoDTO[]> allTodoItems = restTemplate.getForEntity(String.format("%s?page=0&size=10", getTodoEndpoint()), TodoDTO[].class);
		assertThat(allTodoItems.getBody(), is(notNullValue()));

		List<TodoDTO> todoDTOS = Arrays.stream(allTodoItems.getBody()).toList();

		assertThat(allTodoItems.getStatusCode(), is(OK));
		assertThat(todoDTOS, hasSize(5));
	}

	@Test
	void testGetPastDueItems() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		IntStream.rangeClosed(1, 5).forEach(i -> restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class));

		TodoDTO pastDueTodo = TodoDTO.builder()
				.status(Status.PAST_DUE)
				.dueAt(LocalDateTime.now().minusDays(2))
				.build();

		IntStream.rangeClosed(1, 2).forEach(i -> restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), i), PATCH, getRequestEntity(pastDueTodo), TodoDTO.class));

		ResponseEntity<TodoDTO[]> allPastDuoTodoItems = restTemplate.getForEntity(String.format("%s?onlyNotDone=true&page=0&size=10", getTodoEndpoint()), TodoDTO[].class);
		assertThat(allPastDuoTodoItems.getBody(), is(notNullValue()));

		List<TodoDTO> todoDTOS = Arrays.stream(allPastDuoTodoItems.getBody()).toList();

		assertThat(allPastDuoTodoItems.getStatusCode(), is(OK));
		assertThat(todoDTOS, hasSize(3));
	}

	@Test
	void testGetTodoItem() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();
		assertThat(todo, is(notNullValue()));

		ResponseEntity<TodoDTO> createdTodoResponse = restTemplate.getForEntity(String.format("%s/%d", getTodoEndpoint(), todo.id()), TodoDTO.class);
		TodoDTO createdTodo = createdTodoResponse.getBody();

		assertThat(createdTodo, is(notNullValue()));
		assertThat(createdTodoResponse.getStatusCode(), is(OK));
		assertThat(todo.description(), is("New Todo Item"));
		assertThat(todo.status(), is(Status.NOT_DONE));
		assertThat(todo.createdAt(), lessThan(LocalDateTime.now()));
	}

	@Test
	void testAutomaticallyChangeOfStatusOfPastDueItems() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO patchedPastDueTodo = TodoDTO.builder()
				.status(Status.PAST_DUE)
				.dueAt(LocalDateTime.now().minusDays(2))
				.build();
		restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(patchedPastDueTodo), TodoDTO.class);

		ResponseEntity<TodoDTO> pastDueTodoResponse = restTemplate.getForEntity(String.format("%s/%d", getTodoEndpoint(), todo.id()), TodoDTO.class);
		TodoDTO pastDueTodo = pastDueTodoResponse.getBody();

		assertThat(pastDueTodo, is(notNullValue()));
		assertThat(pastDueTodoResponse.getStatusCode(), is(OK));
		assertThat(pastDueTodo.status(), is(Status.PAST_DUE));
	}

	@Test
	void testChangePastDueItem() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO patchedPastDueTodo = TodoDTO.builder()
				.status(Status.PAST_DUE)
				.dueAt(LocalDateTime.now().minusDays(2))
				.build();
		restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(patchedPastDueTodo), TodoDTO.class);

		TodoDTO newPatchedPastDueTodo = TodoDTO.builder()
				.dueAt(LocalDateTime.now())
				.build();

		assertThrows(HttpClientErrorException.BadRequest.class, () -> restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(newPatchedPastDueTodo), Object.class));
	}

	@Test
	void testGetNonExistentTodoItemChangePastDueItem() {
		assertThrows(HttpClientErrorException.NotFound.class, () -> restTemplate.getForEntity(String.format("%s/%d", getTodoEndpoint(), 10L), Object.class));
	}

	@Test
	void testAddTodoItemWithoutDescription() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description(null)
				.build();
		assertThrows(HttpClientErrorException.BadRequest.class, () -> restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class));
	}

	@Test
	void testUpdateDoneItem() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO patchedPastDueTodo = TodoDTO.builder()
				.status(Status.DONE)
				.build();
		restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(patchedPastDueTodo), TodoDTO.class);

		TodoDTO newPatchedPastDueTodo = TodoDTO.builder()
				.description("New Description")
				.build();

		assertThrows(HttpClientErrorException.BadRequest.class, () -> restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(newPatchedPastDueTodo), Object.class));
	}

	@Test
	void testUpdatePastDueItemWithFutureDate() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO patchedPastDueTodo = TodoDTO.builder()
				.status(Status.PAST_DUE)
				.dueAt(LocalDateTime.now().plusDays(2))
				.build();

		assertThrows(HttpClientErrorException.BadRequest.class, () -> restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(patchedPastDueTodo), TodoDTO.class));
	}

	@Test
	void testUpdatePastDueStatusChange() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO patchedPastDueTodo = TodoDTO.builder()
				.dueAt(LocalDateTime.now().minusDays(2))
				.build();

		assertThrows(HttpClientErrorException.BadRequest.class, () -> restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(patchedPastDueTodo), TodoDTO.class));
	}

	@Test
	void testChangeStatusToPastDueWithoutSettingTheDueAtDate() {
		TodoDTO todoDTO = TodoDTO.builder()
				.description("New Todo Item")
				.build();

		ResponseEntity<TodoDTO> response = restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
		TodoDTO todo = response.getBody();
		assertThat(todo, is(notNullValue()));

		TodoDTO patchedPastDueTodo = TodoDTO.builder()
				.status(Status.PAST_DUE)
				.build();

		assertThrows(HttpClientErrorException.BadRequest.class, () -> restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(patchedPastDueTodo), TodoDTO.class));
	}

	private static HttpEntity<TodoDTO> getRequestEntity(TodoDTO todoDTO) {
		return new HttpEntity<>(todoDTO, getHttpHeaders());
	}

	private static HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		return headers;
	}

	private String getTodoEndpoint() {
		return "http://localhost:" + port + "/todo-service/todo";
	}

}
