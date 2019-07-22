package com.bhanuchaddha.architecture.knapsackservice.data.entity;

import com.bhanuchaddha.architecture.knapsackservice.dto.TimestampsDto;
import lombok.Builder;
import lombok.Setter;
import lombok.ToString;

import java.time.Clock;
import java.time.Instant;

@Builder
@ToString
@Setter
public class Timestamps {
    private Long submitted;
    private Long started;
    private Long completed;


    public static Timestamps initialValue(Clock clock){
        return Timestamps.builder()
                .submitted(Instant.now(clock).getEpochSecond())
                .started(null)
                .completed(null)
                .build();
    }

    TimestampsDto toTimestampsDto(){
        return TimestampsDto.builder()
                .submitted(submitted)
                .started(started)
                .completed(completed)
                .build();
    }
}
