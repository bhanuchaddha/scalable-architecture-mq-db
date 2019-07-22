package com.bhanuchaddha.architecture.knapsackservice.dto;

import com.bhanuchaddha.architecture.knapsackservice.data.entity.Timestamps;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TimestampsDto {
    private Long submitted;
    private Long started;
    private Long completed;

    @JsonIgnore
    Timestamps toTimestamps(){
        return Timestamps.builder()
                .submitted(submitted)
                .started(started)
                .completed(completed)
                .build();
    }
}


