package org.entur.enlil.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import org.entur.enlil.security.spi.UserContextService;
import org.entur.oauth2.AuthorizedWebClientBuilder;
import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolverBuilder;
import org.entur.oauth2.user.DefaultJwtUserInfoExtractor;
import org.entur.ror.permission.RemoteBabaRoleAssignmentExtractor;
import org.entur.ror.permission.RemoteBabaUserInfoExtractor;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("entur")
public class EnturSecurityConfiguration {

  @ConditionalOnProperty(
    value = "enlil.security.role.assignment.extractor",
    havingValue = "jwt",
    matchIfMissing = true
  )
  @Bean
  public RoleAssignmentExtractor jwtRoleAssignmentExtractor() {
    return new JwtRoleAssignmentExtractor();
  }

  @ConditionalOnProperty(
    value = "enlil.security.role.assignment.extractor",
    havingValue = "baba"
  )
  @Bean
  public RoleAssignmentExtractor babaRoleAssignmentExtractor(
    @Qualifier("internalWebClient") WebClient webClient,
    @Value("${user.permission.rest.service.url}") String url
  ) {
    return new RemoteBabaRoleAssignmentExtractor(webClient, url);
  }

  @ConditionalOnProperty(
    value = "enlil.security.role.assignment.extractor",
    havingValue = "baba"
  )
  @Bean("internalWebClient")
  WebClient internalWebClient(
    WebClient.Builder webClientBuilder,
    OAuth2ClientProperties properties,
    @Value("${ror.oauth2.client.audience}") String audience
  ) {
    return new AuthorizedWebClientBuilder(webClientBuilder)
      .withOAuth2ClientProperties(properties)
      .withAudience(audience)
      .withClientRegistrationId("internal")
      .build();
  }

  @ConditionalOnProperty(
    value = "enlil.security.role.assignment.extractor",
    havingValue = "jwt",
    matchIfMissing = true
  )
  @Bean
  public UserInfoExtractor jwtUserInfoExtractor() {
    return new DefaultJwtUserInfoExtractor();
  }

  @ConditionalOnProperty(
    value = "enlil.security.role.assignment.extractor",
    havingValue = "baba"
  )
  @Bean
  public UserInfoExtractor babaUserInfoExtractor(
    @Qualifier("internalWebClient") WebClient webClient,
    @Value("${user.permission.rest.service.url}") String url
  ) {
    return new RemoteBabaUserInfoExtractor(webClient, url);
  }

  @Bean
  public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
    @Value(
      "${enlil.oauth2.resourceserver.auth0.entur.partner.jwt.audience:}"
    ) String enturPartnerAuth0Audience,
    @Value(
      "${enlil.oauth2.resourceserver.auth0.entur.partner.jwt.issuer-uri:}"
    ) String enturPartnerAuth0Issuer
  ) {
    return new MultiIssuerAuthenticationManagerResolverBuilder()
      .withEnturPartnerAuth0Issuer(enturPartnerAuth0Issuer)
      .withEnturPartnerAuth0Audiences(parseAudiences(enturPartnerAuth0Audience))
      .build();
  }

  private List<String> parseAudiences(String audiences) {
    if (audiences == null || audiences.trim().isEmpty()) {
      return List.of();
    }
    return Arrays.asList(audiences.split(","));
  }

  @Bean
  public UserContextService userContextService(
    RoleAssignmentExtractor roleAssignmentExtractor
  ) {
    return new EnturUserContextService(roleAssignmentExtractor);
  }
}
