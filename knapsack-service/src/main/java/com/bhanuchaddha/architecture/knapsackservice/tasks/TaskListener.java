package com.bhanuchaddha.architecture.knapsackservice.tasks;

import com.bhanuchaddha.architecture.knapsackservice.data.TaskRepository;
import com.bhanuchaddha.architecture.knapsackservice.data.entity.Status;
import com.bhanuchaddha.architecture.knapsackservice.dto.TaskDto;
import com.bhanuchaddha.architecture.knapsackservice.knapsack.KnapsackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskListener implements MessageListener {

    private final TaskRepository repository;
    @Qualifier("cetClock")
    private final Clock clock;
    private final KnapsackService knapsackService;


    private ObjectMapper objectMapper = new ObjectMapper();


    public void onMessage(final Message message, final byte[] pattern) {
        try {

            TaskDto task = objectMapper.readValue(message.toString(), TaskDto.class);

            repository.findById(task.getTask())
                    .filter(t->t.getStatus().equals(Status.SUBMITTED)) // It will prevent the duplicated processing
                    .flatMapSingle(t-> repository.save(t.updateStatus(Status.STARTED,clock)))
                    .flatMap(knapsackService::solve)
                    .map(t-> t.updateStatus(Status.COMPLETED, clock))
                    .flatMap(t -> repository.save(t))
                    .doOnError(throwable -> log.error("Error while processing task", throwable))
                    .subscribe();

        } catch (IOException e) {
            //TODO: Better exception handling
            throw new RuntimeException("Message could not be parsed", e);
        }
        log.info("Message received: " + new String(message.getBody()));
    }
}
