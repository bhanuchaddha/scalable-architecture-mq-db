package com.bhanuchaddha.architecture.taskservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ApplicationConfiguration {

    // To make code more testable
    @Bean("cetClock")
    public Clock createClock(){
        return Clock.system(ZoneId.of("CET"));
    }
}
