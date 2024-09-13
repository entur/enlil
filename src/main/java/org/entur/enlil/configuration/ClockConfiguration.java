package org.entur.enlil.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;

@Configuration
@Profile("!test")
public class ClockConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
