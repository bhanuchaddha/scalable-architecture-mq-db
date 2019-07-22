package com.bhanuchaddha.architecture.knapsackservice.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("messaging")
@Getter @Setter
public class MessagingProperties {
    private String queue;
}
