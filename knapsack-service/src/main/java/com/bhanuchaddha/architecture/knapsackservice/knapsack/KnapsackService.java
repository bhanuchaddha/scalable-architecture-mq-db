package com.bhanuchaddha.architecture.knapsackservice.knapsack;

import com.bhanuchaddha.architecture.knapsackservice.data.entity.Task;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KnapsackService {

    private final KnapsackSolver knapsackSolver;


    public Single<Task> solve(Task task){
        return Single.just(task)
                .observeOn(Schedulers.computation()) // does the calculation on computational thread
                .map(t -> t.updateSolution(knapsackSolver.solve(task.getProblem())))
                .map(t->{
                    // To Simulate long running calculation
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info(t.getTask()+" is being processed");
                    return t;
                })

                ;
    }
}
