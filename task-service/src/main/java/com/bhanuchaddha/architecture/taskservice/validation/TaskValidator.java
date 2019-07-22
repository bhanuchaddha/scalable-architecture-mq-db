package com.bhanuchaddha.architecture.taskservice.validation;

import com.bhanuchaddha.architecture.taskservice.dto.ProblemDto;
import org.springframework.stereotype.Component;

//TODO: Better Validation Architecture
@Component
public class TaskValidator {

    public void validate(ProblemDto problem){
        if(problem.getValues().size()!= problem.getWeights().size()){
            throw new IllegalArgumentException("values and weights array should have same size");
        }

    }
}
