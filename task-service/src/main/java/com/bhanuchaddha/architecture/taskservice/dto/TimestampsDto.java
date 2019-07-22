package com.bhanuchaddha.architecture.taskservice.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TimestampsDto {
    private Long submitted;
    private Long started;
    private Long completed;
}
