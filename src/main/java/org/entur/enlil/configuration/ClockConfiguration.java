package org.entur.enlil.configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ClockConfiguration {

  @Bean
  Clock clock() {
    return Clock.system(ZoneId.of(ZoneOffset.UTC.getId()));
  }
}
