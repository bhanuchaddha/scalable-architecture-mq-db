package com.bhanuchaddha.architecture.taskservice.data.entity;

import com.bhanuchaddha.architecture.taskservice.dto.TaskDto;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Builder
@Document
public class Task {
    @Id
    @Builder.Default
    private  String task = RandomStringUtils.randomAlphanumeric(8);
    @Builder.Default
    private  Status status = Status.SUBMITTED;
    private  Timestamps timestamps;
    private  Problem problem;
    private  Solution solution;


    public TaskDto toTaskDto(){
        return TaskDto.builder()
                .task(task)
                .status(status)
                .timestamps(timestamps.toTimestampsDto())
                .problem(problem.toProblemDto())
                .solution(solution!=null ? solution.toSolutionDto():null)
                .build();
    }

}
