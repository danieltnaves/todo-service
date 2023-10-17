package com.danieltnaves.todoservice.todo.errors.configuration;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AfterThrowingTodoServiceExceptionAspect {

    @AfterThrowing(pointcut = "execution(* com.danieltnaves.todoservice.todo.TodoService.*(..))", throwing = "ex")
    public void afterThrowingTodoServiceException(Exception ex) {
        log.error("An exception has been raised on TodoService. Message: {}", ex.getMessage());
    }
}
