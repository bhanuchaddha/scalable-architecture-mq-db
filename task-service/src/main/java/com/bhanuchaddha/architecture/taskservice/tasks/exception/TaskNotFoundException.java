package com.bhanuchaddha.architecture.taskservice.tasks.exception;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(String message) {
        super(message);
    }
}
