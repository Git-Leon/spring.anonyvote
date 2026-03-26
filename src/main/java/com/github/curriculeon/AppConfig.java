package com.github.curriculeon;

import org.springframework.context.annotation.Bean;

import java.time.Clock;

/**
 * Application configuration. Added a Clock bean so time-based logic (24-hour visibility)
 * can be tested and injected.
 */
@org.springframework.context.annotation.Configuration
public class AppConfig {
    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
