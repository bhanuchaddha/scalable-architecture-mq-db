package com.bhanuchaddha.architecture.knapsackservice.data.entity;

import com.bhanuchaddha.architecture.knapsackservice.dto.SolutionDto;
import lombok.Builder;

import java.math.BigInteger;
import java.util.List;

@Builder
public class Solution {
    private List<Integer> packedItems;
    private BigInteger totalValue;

    public SolutionDto toSolutionDto(){
        return SolutionDto.builder()
                .packedItems(packedItems)
                .totalValue(totalValue)
                .build();
    }
}
