package org.entur.enlil.configuration;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockedClockConfiguration {

  private final Clock clock;

  public MockedClockConfiguration() {
    clock = Mockito.mock(Clock.class);
    Mockito.when(clock.instant()).thenReturn(Instant.parse("2023-09-19T19:19:00Z"));
    Mockito.when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
  }

  @Bean
  public Clock clock() {
    return clock;
  }
}
