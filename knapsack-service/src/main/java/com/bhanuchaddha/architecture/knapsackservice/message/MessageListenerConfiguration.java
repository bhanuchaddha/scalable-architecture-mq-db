package com.bhanuchaddha.architecture.knapsackservice.message;

import com.bhanuchaddha.architecture.knapsackservice.dto.TaskDto;
import com.bhanuchaddha.architecture.knapsackservice.tasks.TaskListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableConfigurationProperties({MessagingProperties.class})
@RequiredArgsConstructor
public class MessageListenerConfiguration {

    private final TaskListener taskListener;
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
    MessageListenerAdapter messageListener( ) {
        return new MessageListenerAdapter(taskListener);
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        container.setRecoveryInterval(2000000);
        return container;
    }
}
