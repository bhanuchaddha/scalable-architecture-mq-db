package com.bhanuchaddha.architecture.knapsackservice.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor @Getter @Setter
public class TaskRequest {
    private  ProblemDto problem;
}
