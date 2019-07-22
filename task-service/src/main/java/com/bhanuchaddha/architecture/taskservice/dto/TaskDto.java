package com.bhanuchaddha.architecture.taskservice.dto;

import com.bhanuchaddha.architecture.taskservice.data.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TaskDto {
    private  String task;
    private  Status status;
    private  TimestampsDto timestamps;
    private  ProblemDto problem;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private  SolutionDto solution;


}
