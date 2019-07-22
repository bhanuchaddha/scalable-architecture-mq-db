package com.bhanuchaddha.architecture.knapsackservice.dto;

import com.bhanuchaddha.architecture.knapsackservice.data.entity.Solution;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SolutionDto {
    private List<Integer> packedItems;
    private BigInteger totalValue;

    @JsonIgnore
    public Solution toSolution(){
        return Solution.builder()
                .packedItems(packedItems)
                .totalValue(totalValue)
                .build();
    }
}
