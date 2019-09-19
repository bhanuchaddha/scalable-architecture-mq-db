package com.bhanuchaddha.architecture.knapsackservice.tasks;

import com.bhanuchaddha.architecture.knapsackservice.data.TaskRepository;
import com.bhanuchaddha.architecture.knapsackservice.data.entity.Status;
import com.bhanuchaddha.architecture.knapsackservice.dto.TaskDto;
import com.bhanuchaddha.architecture.knapsackservice.knapsack.KnapsackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskListener  {

    private final TaskRepository repository;
    @Qualifier("cetClock")
    private final Clock clock;
    private final KnapsackService knapsackService;
    private ObjectMapper mapper = new ObjectMapper();


    @KafkaListener(topics = "knapsack-topic", clientIdPrefix = "json",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(String task) {
        try {
            TaskDto taskDto = mapper.readValue(task, TaskDto.class);

            repository.findById(taskDto.getTask())
                    .filter(t->t.getStatus().equals(Status.SUBMITTED)) // It will prevent the duplicated processing
                    .flatMapSingle(t-> repository.save(t.updateStatus(Status.STARTED,clock)))
                    .flatMap(knapsackService::solve)
                    .map(t-> t.updateStatus(Status.COMPLETED, clock))
                    .flatMap(t -> repository.save(t))
                    .doOnError(throwable -> log.error("Error while processing task", throwable))
                    .subscribe();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String typeIdHeader(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.key().equals("__TypeId__"))
                .findFirst().map(header -> new String(header.value())).orElse("N/A");
    }
}
