package org.entur.enlil.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Authentication and authorization configuration for Enlil.
 * All requests must be authenticated except for the Actuator and /siri (xml) endpoints.
 */

@Profile("!local-no-authentication & !test")
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Component
public class EnlilSecurityConfiguration {

  @Bean
  public SecurityFilterChain filterChain(
    HttpSecurity http,
    AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver
  ) throws Exception {
    return http
      .csrf(csrf -> csrf.ignoringRequestMatchers("/siri"))
      .authorizeHttpRequests(auth ->
        auth
          .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/prometheus"))
          .permitAll()
          .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/health"))
          .permitAll()
          .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/health/liveness"))
          .permitAll()
          .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/health/readiness"))
          .permitAll()
          .requestMatchers(AntPathRequestMatcher.antMatcher("/siri"))
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .oauth2ResourceServer(configurer ->
        configurer.authenticationManagerResolver(authenticationManagerResolver)
      )
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .build();
  }

  private CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOriginPattern("*");
    configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @ConditionalOnMissingBean
  @Bean
  public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
    @Value("${enlil.security.jwt.issuer-uri}") String issuer
  ) {
    return JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers(List.of(issuer));
  }
}
