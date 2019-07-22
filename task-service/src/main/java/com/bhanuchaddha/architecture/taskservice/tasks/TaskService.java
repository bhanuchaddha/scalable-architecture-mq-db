package com.bhanuchaddha.architecture.taskservice.tasks;

import com.bhanuchaddha.architecture.taskservice.data.TaskRepository;
import com.bhanuchaddha.architecture.taskservice.data.entity.Task;
import com.bhanuchaddha.architecture.taskservice.data.entity.Timestamps;
import com.bhanuchaddha.architecture.taskservice.message.MessagingService;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final MessagingService messagingService;

    @Qualifier("cetClock")
    private final Clock clock;

    public Single<Task> createTask(CreateTaskRequest request){
            return taskRepository.save( Task.builder()
                    .timestamps(Timestamps.initialValue(clock))
                    .problem(request.getProblem().toProblem())
                    .build()
            ).doOnSuccess(task -> messagingService.queueTask(task.toTaskDto()));
    }

    public Maybe<Task> getTask(GetTaskRequest request){
        return taskRepository.findById(request.getTaskId());
    }

    public Flowable<Task> getAllTasks(){
        return taskRepository.findAll();
    }
}
