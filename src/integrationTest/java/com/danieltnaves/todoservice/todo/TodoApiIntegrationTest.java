package com.danieltnaves.todoservice.todo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.danieltnaves.todoservice.todo.api.TodoDTO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoApiIntegrationTest {

	public static final String NEW_TODO_ITEM = "New Todo Item";

	public static final String NEW_TODO_ITEM_DESCRIPTION = "New Todo Item description";

	public static final String NEW_DESCRIPTION = "New Description";

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
		ResponseEntity<TodoDTO> response = createNewTodoItem(getNewTodoItem());
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getStatusCode(), is(CREATED));
		assertThat(response.getBody().description(), is(NEW_TODO_ITEM));
		assertThat(response.getBody().status(), is(TodoDTO.Status.NOT_DONE));
		assertThat(response.getBody().createdAt(), lessThan(LocalDateTime.now()));
	}

	@Test
	void testChangeDescription() {
		TodoDTO todo = createNewTodoItem(getNewTodoItem()).getBody();
		ResponseEntity<TodoDTO> response = updateTodoItem(Objects.requireNonNull(todo), getTodoItemWithNewDescription());
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getStatusCode(), is(OK));
		assertThat(response.getBody().description(), is(NEW_TODO_ITEM_DESCRIPTION));
	}


	@Test
	void testMarkAnItemAsDone() {
		ResponseEntity<TodoDTO> response = updateTodoItem(Objects.requireNonNull(createNewTodoItem(getNewTodoItem()).getBody()), getTodoItemMarkedAsDone());
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getStatusCode(), is(OK));
		assertThat(response.getBody().status(), is(TodoDTO.Status.DONE));
	}

	@Test
	void testGetAllItems() {
		IntStream.rangeClosed(1, 5).forEach(i -> createNewTodoItem(getNewTodoItem()));
		ResponseEntity<TodoDTO[]> allTodoItems = getItemsWithPagination(false, 0, 10);
		assertThat(allTodoItems.getBody(), is(notNullValue()));
		assertThat(allTodoItems.getStatusCode(), is(OK));
		assertThat(Arrays.stream(allTodoItems.getBody()).toList(), hasSize(5));
	}

	@Test
	void testGetPastDueItems() {
		IntStream.rangeClosed(1, 5).forEach(i -> createNewTodoItem(getNewTodoItem()));
		IntStream.rangeClosed(1, 2).forEach(this::updateItemToPastDue);
		ResponseEntity<TodoDTO[]> allPastDuoTodoItems = getItemsWithPagination(true, 0, 10);
		assertThat(allPastDuoTodoItems.getStatusCode(), is(OK));
		assertThat(Arrays.stream(Objects.requireNonNull(allPastDuoTodoItems.getBody())).toList(), hasSize(3));
	}

	@Test
	void testGetTodoItem() {
		ResponseEntity<TodoDTO> retrievedTodoItemResponse = retrieveTodoItem(createNewTodoItem(getNewTodoItem()).getBody());
		TodoDTO retrievedTodoItem = retrievedTodoItemResponse.getBody();
		assertThat(retrievedTodoItemResponse.getStatusCode(), is(OK));
		assertThat(retrievedTodoItem, is(notNullValue()));
		assertThat(retrievedTodoItem.description(), is(NEW_TODO_ITEM));
		assertThat(retrievedTodoItem.status(), is(TodoDTO.Status.NOT_DONE));
		assertThat(retrievedTodoItem.createdAt(), lessThan(LocalDateTime.now()));
	}

	@Test
	void testAutomaticallyChangeOfStatusOfPastDueItems() {
		ResponseEntity<TodoDTO> newTodoItemResponse = createNewTodoItem(getNewTodoItem());
		updateTodoItem(Objects.requireNonNull(newTodoItemResponse.getBody()), getPastDueTodoItem());
		ResponseEntity<TodoDTO> pastDueTodoItemResponse = retrieveTodoItem(Objects.requireNonNull(newTodoItemResponse).getBody());
		TodoDTO pastDueTodo = pastDueTodoItemResponse.getBody();
		assertThat(pastDueTodoItemResponse.getStatusCode(), is(OK));
		assertThat(pastDueTodo, is(notNullValue()));
		assertThat(pastDueTodo.status(), is(TodoDTO.Status.PAST_DUE));
	}

	@Test
	void testChangePastDueItem() {
		TodoDTO todo = createNewTodoItem(getNewTodoItem()).getBody();
		updateTodoItem(Objects.requireNonNull(todo), getPastDueTodoItem());
		assertThrows(HttpClientErrorException.BadRequest.class, () ->
				updateTodoItem(todo, getNewPatchedPastDueTodoItem()));
	}

	@Test
	void testGetNonExistentTodoItemChangePastDueItem() {
		assertThrows(HttpClientErrorException.NotFound.class, () ->
				restTemplate.getForEntity(String.format("%s/%d", getTodoEndpoint(), 10L), Object.class));
	}

	@Test
	void testAddTodoItemWithoutDescription() {
		assertThrows(HttpClientErrorException.BadRequest.class, () -> createNewTodoItem(getTodoItemWithNullDescription()));
	}

	@Test
	void testUpdateDoneItem() {
		TodoDTO todo = createNewTodoItem(getNewTodoItem()).getBody();
		updateTodoItem(Objects.requireNonNull(todo), getTodoItemMarkedAsDone());
		assertThrows(HttpClientErrorException.BadRequest.class, () ->
				updateTodoItem(todo, getNewTodoItemWithPatchedDescription()));
	}

	@Test
	void testUpdatePastDueItemWithFutureDate() {
		assertThrows(HttpClientErrorException.BadRequest.class, () ->
				updateTodoItem(Objects.requireNonNull(createNewTodoItem(getNewTodoItem()).getBody()), getTodoItemWithPastDueStatusAndFutureDate()));
	}

	@Test
	void testUpdatePastDueStatusChange() {
		assertThrows(HttpClientErrorException.BadRequest.class, () ->
				updateTodoItem(Objects.requireNonNull(createNewTodoItem(getNewTodoItem()).getBody()), getPatchedPastDueTodoItem()));
	}

	@Test
	void testChangeStatusToPastDueWithoutSettingTheDueAtDate() {
		assertThrows(HttpClientErrorException.BadRequest.class, () ->
				updateTodoItem(Objects.requireNonNull(createNewTodoItem(getNewTodoItem()).getBody()), getTodoItemWithPastDueStatus()));
	}

	private ResponseEntity<TodoDTO> createNewTodoItem(TodoDTO todoDTO) {
		return restTemplate.postForEntity(getTodoEndpoint(), todoDTO, TodoDTO.class);
	}

	private static TodoDTO getNewTodoItem() {
		return TodoDTO.builder()
				.description(NEW_TODO_ITEM)
				.build();
	}

	private static TodoDTO getTodoItemWithNewDescription() {
		return TodoDTO.builder()
				.description(NEW_TODO_ITEM_DESCRIPTION)
				.build();
	}

	private ResponseEntity<TodoDTO> updateTodoItem(TodoDTO todo, TodoDTO todoDTO) {
		return restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), todo.id()), PATCH, getRequestEntity(todoDTO), TodoDTO.class);
	}

	private static TodoDTO getTodoItemMarkedAsDone() {
		return TodoDTO.builder()
				.status(TodoDTO.Status.DONE)
				.build();
	}

	private ResponseEntity<TodoDTO[]> getItemsWithPagination(boolean onlyNotDone, int page, int size) {
		return restTemplate.getForEntity(String.format("%s?onlyNotDone=%b&page=%d&size=%d", getTodoEndpoint(), onlyNotDone, page, size), TodoDTO[].class);
	}

	private void updateItemToPastDue(int i) {
		restTemplate.exchange(String.format("%s/%d", getTodoEndpoint(), i), PATCH, getRequestEntity(getPastDueTodoItem()), TodoDTO.class);
	}

	private static TodoDTO getPastDueTodoItem() {
		return TodoDTO.builder()
				.status(TodoDTO.Status.PAST_DUE)
				.dueAt(LocalDateTime.now().minusDays(2))
				.build();
	}

	private ResponseEntity<TodoDTO> retrieveTodoItem(TodoDTO todo) {
		return restTemplate.getForEntity(String.format("%s/%d", getTodoEndpoint(), Objects.requireNonNull(todo).id()), TodoDTO.class);
	}

	private static TodoDTO getNewPatchedPastDueTodoItem() {
		return TodoDTO.builder()
				.dueAt(LocalDateTime.now())
				.build();
	}

	private static TodoDTO getTodoItemWithNullDescription() {
		return TodoDTO.builder()
				.description(null)
				.build();
	}

	private static TodoDTO getNewTodoItemWithPatchedDescription() {
		return TodoDTO.builder()
				.description(NEW_DESCRIPTION)
				.build();
	}

	private static TodoDTO getTodoItemWithPastDueStatusAndFutureDate() {
		return TodoDTO.builder()
				.status(TodoDTO.Status.PAST_DUE)
				.dueAt(LocalDateTime.now().plusDays(2))
				.build();
	}

	private static TodoDTO getPatchedPastDueTodoItem() {
		return TodoDTO.builder()
				.dueAt(LocalDateTime.now().minusDays(2))
				.build();
	}

	private static TodoDTO getTodoItemWithPastDueStatus() {
		return TodoDTO.builder()
				.status(TodoDTO.Status.PAST_DUE)
				.build();
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
