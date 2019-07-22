package com.bhanuchaddha.architecture.taskservice.data.entity;

import com.bhanuchaddha.architecture.taskservice.dto.ProblemDto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Builder @Getter
public class Problem {
    private BigInteger capacity;
    private List<BigInteger> weights;
    private  List<BigInteger> values;

    public ProblemDto toProblemDto(){
        return ProblemDto.builder()
                .capacity(capacity)
                .weights(weights)
                .values(values)
                .build();
    }
}
