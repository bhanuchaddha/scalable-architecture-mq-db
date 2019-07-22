package com.bhanuchaddha.architecture.knapsackservice.knapsack;

import com.bhanuchaddha.architecture.knapsackservice.data.entity.Problem;
import com.bhanuchaddha.architecture.knapsackservice.data.entity.Solution;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic Implementation
 */
//TODO: Add validations
@Component
public class KnapsackSolver {

    public Solution solve(Problem problem){

        int nItems = problem.getValues().size();
        BigInteger capacity = problem.getCapacity();
        List<BigInteger> weights = problem.getWeights();
        List<BigInteger> values = problem.getValues();

        int[][] matrix = new int[nItems + 1][problem.getCapacity().intValue() + 1];

        for (int i = 0; i <= capacity.intValue(); i++)
            matrix[0][i] = 0;

        for (int i = 1; i <= nItems; i++) {
            for (int j = 0; j <= capacity.intValue(); j++) {
                if (weights.get(i-1).intValue() > j)
                    matrix[i][j] = matrix[i-1][j];
                else
                    matrix[i][j] = Math.max(matrix[i-1][j], matrix[i-1][j - weights.get(i-1).intValue() ]
                            + values.get(i-1).intValue() );
            }
        }

        int res = matrix[nItems][capacity.intValue()];
        int w = capacity.intValue();
        List<Integer> itemsSolution = new ArrayList<>();

        for (int i = nItems; i > 0  &&  res > 0; i--) {
            if (res != matrix[i-1][w]) {
                itemsSolution.add(i-1);
                res -= values.get(i-1).intValue();
                w -= weights.get(i-1).intValue();
            }
        }

        return Solution.builder()
                .packedItems(itemsSolution)
                .totalValue(BigInteger.valueOf(matrix[nItems][capacity.intValue()]))
                .build();
    }
}
