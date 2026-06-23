package org.entur.enlil.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.client.autoconfigure.reactive.ReactiveHttpClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.webclient.autoconfigure.WebClientAutoConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Guards the Spring Boot 4 dependency on spring-boot-webclient. Its
 * WebClientAutoConfiguration provides the WebClient.Builder bean that
 * EnturSecurityConfiguration (baba role assignment extractor) requires.
 */
class WebClientBuilderAvailabilityTest {

  @Test
  void webClientBuilderBeanIsAutoConfigured() {
    new ApplicationContextRunner()
      .withConfiguration(
        AutoConfigurations.of(
          ReactiveHttpClientAutoConfiguration.class,
          WebClientAutoConfiguration.class
        )
      )
      .run(context -> assertThat(context).hasSingleBean(WebClient.Builder.class));
  }
}
