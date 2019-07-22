package com.bhanuchaddha.architecture.knapsackservice.dto;

import com.bhanuchaddha.architecture.knapsackservice.data.entity.Status;
import com.bhanuchaddha.architecture.knapsackservice.data.entity.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TaskDto {
    private  String task;
    private Status status;
    private  TimestampsDto timestamps;
    private  ProblemDto problem;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private  SolutionDto solution;

    @JsonIgnore
    public Task toTask(){
        return Task.builder()
                .task(task)
                .status(status)
                .timestamps(timestamps.toTimestamps())
                .problem(problem.toProblem())
                .solution(solution!=null ? solution.toSolution():null)
                .build();
    }


}
