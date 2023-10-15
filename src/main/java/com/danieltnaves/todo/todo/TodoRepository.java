package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends CrudRepository<Todo, Long>, PagingAndSortingRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t WHERE t.status = :status")
    Page<Todo> findAllByStatus(Todo.Status status, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Todo t SET t.status = :status WHERE t.id = :id")
    void updateTodoStatusById(@Param("id") Long id, @Param("status") Todo.Status status);
}
