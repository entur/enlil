package org.entur.enlil.siri.configuration;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ClockConfiguration {

  @Bean
  Clock clock() {
    return Clock.systemDefaultZone();
  }
}
