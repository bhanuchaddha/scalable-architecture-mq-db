package com.bhanuchaddha.architecture.taskservice.message;

import com.bhanuchaddha.architecture.taskservice.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessagingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic knapsackTopic;

    public void queueTask(TaskDto task) {
        kafkaTemplate.send(knapsackTopic.name(), task) ;
    }
}
