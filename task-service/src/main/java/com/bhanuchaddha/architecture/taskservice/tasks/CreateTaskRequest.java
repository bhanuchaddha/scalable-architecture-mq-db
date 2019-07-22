package com.bhanuchaddha.architecture.taskservice.tasks;

import com.bhanuchaddha.architecture.taskservice.data.entity.Problem;
import com.bhanuchaddha.architecture.taskservice.dto.ProblemDto;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class CreateTaskRequest {
    private ProblemDto problem;
}
