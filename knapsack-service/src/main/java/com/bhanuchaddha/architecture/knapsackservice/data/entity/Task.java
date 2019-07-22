package com.bhanuchaddha.architecture.knapsackservice.data.entity;

import com.bhanuchaddha.architecture.knapsackservice.dto.TaskDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Builder
@Document
@Getter
public class Task {
    @Id
    @Builder.Default
    private  String task = UUID.randomUUID().toString();
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

    public Task updateStatus(Status status, Clock clock){
        this.status = status;
        switch (status){
            case STARTED:
                timestamps.setStarted(Instant.now(clock).getEpochSecond());
                break;
            case COMPLETED:
                timestamps.setCompleted(Instant.now(clock).getEpochSecond());
                break;
        }

        return this;
    }

    public Task updateSolution(Solution solution){
        this.solution = solution;
        return this;
    }

}
