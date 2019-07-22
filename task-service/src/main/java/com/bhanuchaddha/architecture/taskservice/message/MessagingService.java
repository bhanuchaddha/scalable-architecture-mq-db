package com.bhanuchaddha.architecture.taskservice.message;

import com.bhanuchaddha.architecture.taskservice.dto.TaskDto;
import io.reactivex.Completable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessagingService {

    private final RedisTemplate<String, TaskDto> redisTemplate;
    private final ChannelTopic topic;

    public void queueTask(TaskDto task) {
        redisTemplate.convertAndSend(topic.getTopic(), task) ;
    }
}
