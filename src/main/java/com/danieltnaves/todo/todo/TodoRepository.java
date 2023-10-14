package com.danieltnaves.todo.todo;

import com.danieltnaves.todo.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TodoRepository extends CrudRepository<Todo, Long>, PagingAndSortingRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t WHERE t.status = :status")
    Page<Todo> findAllByStatus(Todo.Status status, Pageable pageable);
}
