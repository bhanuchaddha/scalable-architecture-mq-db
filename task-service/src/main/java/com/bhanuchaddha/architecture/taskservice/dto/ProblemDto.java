package com.bhanuchaddha.architecture.taskservice.dto;

import com.bhanuchaddha.architecture.taskservice.data.entity.Problem;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor @Getter @Setter
public class ProblemDto {

    @NonNull
    private  BigInteger capacity;
    @NonNull
    private  List<BigInteger> weights;
    @NonNull
    private  List<BigInteger> values;

    public Problem toProblem(){
        return Problem.builder()
                .capacity(capacity)
                .weights(weights)
                .values(values)
                .build();
    }

}
