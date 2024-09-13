package org.entur.enlil.security;

import jakarta.servlet.http.HttpServletRequest;
import org.entur.enlil.security.spi.UserContextService;
import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.oauth2.RorAuthenticationConverter;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolverBuilder;
import org.entur.oauth2.user.JwtUserInfoExtractor;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@Profile("entur")
public class EnturSecurityConfiguration {

  @Bean
  public JwtAuthenticationConverter customJwtAuthenticationConverter() {
    return new RorAuthenticationConverter();
  }

  @Bean
  public RoleAssignmentExtractor roleAssignmentExtractor() {
    return new JwtRoleAssignmentExtractor();
  }

  @Bean
  public UserInfoExtractor userInfoExtractor() {
    return new JwtUserInfoExtractor();
  }

  @Bean
  public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
    @Value(
      "${enlil.oauth2.resourceserver.auth0.entur.partner.jwt.audience:}"
    ) String enturPartnerAuth0Audience,
    @Value(
      "${enlil.oauth2.resourceserver.auth0.entur.partner.jwt.issuer-uri:}"
    ) String enturPartnerAuth0Issuer,
    @Value(
      "${enlil.oauth2.resourceserver.auth0.ror.jwt.audience:}"
    ) String rorAuth0Audience,
    @Value(
      "${enlil.oauth2.resourceserver.auth0.ror.jwt.issuer-uri:}"
    ) String rorAuth0Issuer,
    @Value(
      "${enlil.oauth2.resourceserver.auth0.ror.claim.namespace:}"
    ) String rorAuth0ClaimNamespace
  ) {
    return new MultiIssuerAuthenticationManagerResolverBuilder()
      .withEnturPartnerAuth0Issuer(enturPartnerAuth0Issuer)
      .withEnturPartnerAuth0Audience(enturPartnerAuth0Audience)
      .withRorAuth0Issuer(rorAuth0Issuer)
      .withRorAuth0Audience(rorAuth0Audience)
      .withRorAuth0ClaimNamespace(rorAuth0ClaimNamespace)
      .build();
  }

  @Bean
  public UserContextService userContextService(
    RoleAssignmentExtractor roleAssignmentExtractor
  ) {
    return new EnturUserContextService(roleAssignmentExtractor);
  }
}
