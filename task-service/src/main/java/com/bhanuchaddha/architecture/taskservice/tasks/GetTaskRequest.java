package com.bhanuchaddha.architecture.taskservice.tasks;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetTaskRequest {
    private String taskId;

    public static GetTaskRequest of(String taskId){
        return GetTaskRequest.builder()
                .taskId(taskId)
                .build();
    }
}
