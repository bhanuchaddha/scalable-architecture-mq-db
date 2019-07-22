package com.bhanuchaddha.architecture.knapsackservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ApplicationConfiguration {

    @Bean("cetClock")
    public Clock createClock(){
        return Clock.system(ZoneId.of("CET"));
    }
}
