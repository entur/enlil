package org.entur.enlil.configuration;

import org.entur.enlil.security.spi.UserContextService;
import org.entur.enlil.stubs.UserContextServiceStub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfiguration {

  @Bean
  UserContextService userContextService() {
    return new UserContextServiceStub();
  }
}
