package com.bhanuchaddha.architecture.taskservice.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor @Getter @Setter
public class TaskRequest {
    private  ProblemDto problem;
}
