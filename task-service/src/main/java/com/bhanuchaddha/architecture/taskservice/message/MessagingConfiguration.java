package com.bhanuchaddha.architecture.taskservice.message;

import com.bhanuchaddha.architecture.taskservice.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@EnableConfigurationProperties({MessagingProperties.class})
@RequiredArgsConstructor
public class MessagingConfiguration {

    private final MessagingProperties messagingProperties;


    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        return factory;
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(messagingProperties.getQueue());
    }

    @Bean
    public RedisTemplate<String, TaskDto> redisTemplate() {
        final RedisTemplate<String, TaskDto> template = new RedisTemplate<String, TaskDto>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<TaskDto>(TaskDto.class));
        return template;
    }
}
