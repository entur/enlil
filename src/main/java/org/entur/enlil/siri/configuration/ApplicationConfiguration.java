package org.entur.enlil.siri.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
@Profile("!test")
public class ApplicationConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
