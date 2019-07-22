package com.bhanuchaddha.architecture.taskservice.data;

import com.bhanuchaddha.architecture.taskservice.data.entity.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.RxJava2CrudRepository;

public interface TaskRepository extends RxJava2CrudRepository<Task, String> {

}
