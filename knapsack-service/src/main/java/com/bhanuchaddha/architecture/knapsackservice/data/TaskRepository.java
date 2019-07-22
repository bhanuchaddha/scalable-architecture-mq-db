package com.bhanuchaddha.architecture.knapsackservice.data;

import com.bhanuchaddha.architecture.knapsackservice.data.entity.Task;
import org.springframework.data.repository.reactive.RxJava2CrudRepository;

public interface TaskRepository extends RxJava2CrudRepository<Task, String> {

}
