package com.bhanuchaddha.architecture.taskservice;

import com.bhanuchaddha.architecture.taskservice.data.entity.Task;
import com.bhanuchaddha.architecture.taskservice.dto.TaskDto;
import com.bhanuchaddha.architecture.taskservice.dto.TaskRequest;
import com.bhanuchaddha.architecture.taskservice.tasks.CreateTaskRequest;
import com.bhanuchaddha.architecture.taskservice.tasks.GetTaskRequest;
import com.bhanuchaddha.architecture.taskservice.tasks.TaskService;
import com.bhanuchaddha.architecture.taskservice.tasks.exception.TaskNotFoundException;
import com.bhanuchaddha.architecture.taskservice.validation.TaskValidator;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequestMapping("/knapsack")
@RequiredArgsConstructor
public class TaskResource {

    private final TaskService service;
    private final TaskValidator taskValidator;

    @PostMapping
    public DeferredResult<TaskDto> createTask(@RequestBody TaskRequest request){

        DeferredResult<TaskDto> deferredResult = new DeferredResult<TaskDto>();
        taskValidator.validate(request.getProblem());
        service.createTask(CreateTaskRequest.builder()
                .problem(request.getProblem())
                .build()
        ).doOnError(deferredResult::setErrorResult)
                .subscribe(task -> deferredResult.setResult(task.toTaskDto()));

        return deferredResult;
    }

    @GetMapping("/{taskId}")
    public DeferredResult<TaskDto> getTask(@PathVariable("taskId") String taskId){
        DeferredResult<TaskDto> deferredResult = new DeferredResult<TaskDto>();
        service.getTask(GetTaskRequest.of(taskId))
                .switchIfEmpty(Single.error(new TaskNotFoundException(taskId +" not found")))
                .doOnError(deferredResult::setErrorResult)
                .subscribe(task -> deferredResult.setResult(task.toTaskDto()));
        return deferredResult;
    }

    @GetMapping
    public DeferredResult<List<TaskDto>> getAllTasks(){
        DeferredResult<List<TaskDto>> deferredResult = new DeferredResult<List<TaskDto>>();
        service.getAllTasks()
                .map(Task::toTaskDto)
                .toList()
                .doOnError(deferredResult::setErrorResult)
                .subscribe(tasks -> deferredResult.setResult(tasks));
        return deferredResult;
    }
}
