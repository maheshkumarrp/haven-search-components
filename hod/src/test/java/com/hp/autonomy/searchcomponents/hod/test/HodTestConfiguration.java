/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.Authentication;
import com.hp.autonomy.frontend.configuration.AuthenticationConfig;
import com.hp.autonomy.frontend.configuration.BCryptUsernameAndPassword;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.SingleUserAuthentication;
import com.hp.autonomy.hod.client.api.authentication.ApiKey;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationService;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationServiceImpl;
import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.config.HodServiceConfig;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.InMemoryTokenRepository;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.client.token.TokenProxyService;
import com.hp.autonomy.hod.client.token.TokenRepository;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.hod.sso.HodSsoConfig;
import com.hp.autonomy.hod.sso.SpringSecurityTokenProxyService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationConfig;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConditionalOnProperty(value = "mock.hod.authentication", matchIfMissing = true)
public class HodTestConfiguration {
    public static final String HOD_URL = "https://api.havenondemand.com";
    public static final String PROXY_HOST = "web-proxy.sdc.hpecorp.net";
    public static final int PROXY_PORT = 8080;

    public static final String QUERY_PROFILE = "search_default_profile";
    public static final String QUERY_MANIPULATION_INDEX = "search_default_index";
    public static final Set<String> ALLOWED_ORIGINS = Collections.singleton("http://localhost:8080");

    public static final String API_KEY_PROPERTY = "test.api.key";
    public static final String APPLICATION_PROPERTY = "test.application";
    public static final String DOMAIN_PROPERTY = "test.domain";

    @Autowired
    private Environment environment;

    @Autowired
    private HodServiceConfig<?, TokenType.Simple> hodServiceConfig;

    @PostConstruct
    public void init() throws HodErrorException {
        final String apiKey = environment.getProperty(API_KEY_PROPERTY);
        final String application = environment.getProperty(APPLICATION_PROPERTY);
        final String domain = environment.getProperty(DOMAIN_PROPERTY);

        final AuthenticationService authenticationService = new AuthenticationServiceImpl(hodServiceConfig);
        final TokenProxy<EntityType.Application, TokenType.Simple> tokenProxy = authenticationService.authenticateApplication(new ApiKey(apiKey), application, domain, TokenType.Simple.INSTANCE);

        final HodAuthentication authentication = mock(HodAuthentication.class);
        final HodAuthenticationPrincipal hodAuthenticationPrincipal = mock(HodAuthenticationPrincipal.class);
        final ResourceIdentifier identifier = mock(ResourceIdentifier.class);
        when(identifier.toString()).thenReturn(application);
        when(identifier.getDomain()).thenReturn(domain);
        when(hodAuthenticationPrincipal.getApplication()).thenReturn(identifier);
        when(authentication.getPrincipal()).thenReturn(hodAuthenticationPrincipal);
        //noinspection unchecked,rawtypes
        when(authentication.getTokenProxy()).thenReturn((TokenProxy) tokenProxy);

        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Bean
    @Primary
    public ConfigService<HodSearchCapable> configService() {
        @SuppressWarnings("unchecked") final ConfigService<HodSearchCapable> configService = (ConfigService<HodSearchCapable>) mock(ConfigService.class);

        final HodSearchCapable config = mock(HodSearchCapable.class);

        when(config.getQueryManipulation()).thenReturn(new QueryManipulationConfig(QUERY_PROFILE, QUERY_MANIPULATION_INDEX));
        when(config.getFieldsInfo()).thenReturn(new FieldsInfo.Builder().build());

        when(configService.getConfig()).thenReturn(config);

        return configService;
    }

    @Bean
    @Primary
    public ConfigService<HodSsoConfig> hodSsoConfigService() {
        @SuppressWarnings("unchecked") final ConfigService<HodSsoConfig> configService = (ConfigService<HodSsoConfig>) mock(ConfigService.class);

        final HodSsoConfig config = mock(HodSsoConfig.class);

        when(config.getApiKey()).thenReturn(environment.getProperty(API_KEY_PROPERTY));
        when(config.getAllowedOrigins()).thenReturn(ALLOWED_ORIGINS);

        when(configService.getConfig()).thenReturn(config);

        return configService;
    }

    @Bean
    @Primary
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ConfigService<AuthenticationConfig<?>> authenticationConfigService() {
        final ConfigService<AuthenticationConfig<?>> configService = (ConfigService<AuthenticationConfig<?>>) mock(ConfigService.class);

        final AuthenticationConfig<?> config = mock(AuthenticationConfig.class);
        final SingleUserAuthentication login = new SingleUserAuthentication.Builder().setMethod("singleUser").setSingleUser(new BCryptUsernameAndPassword.Builder().setUsername("admin").build()).build();

        when(config.getAuthentication()).thenReturn((Authentication) login);
        when(configService.getConfig()).thenReturn((AuthenticationConfig) config);

        return configService;
    }

    @Bean
    @ConditionalOnMissingBean(HodServiceConfig.class)
    public HodServiceConfig<EntityType.Combined, TokenType.Simple> hodServiceConfig(
            final TokenProxyService<EntityType.Combined, TokenType.Simple> tokenProxyService,
            final HttpClient httpClient, final TokenRepository tokenRepository,
            final ObjectMapper hodSearchResultObjectMapper) {
        return new HodServiceConfig.Builder<EntityType.Combined, TokenType.Simple>(HOD_URL)
                .setTokenProxyService(tokenProxyService)
                .setHttpClient(httpClient)
                .setTokenRepository(tokenRepository)
                .setObjectMapper(hodSearchResultObjectMapper)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(TokenProxyService.class)
    public TokenProxyService<EntityType.Combined, TokenType.Simple> tokenProxyService() {
        return new SpringSecurityTokenProxyService();
    }

    @Bean
    @ConditionalOnMissingBean(HttpClient.class)
    public HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setProxy(new HttpHost(PROXY_HOST, PROXY_PORT))
                .disableCookieManagement()
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(TokenRepository.class)
    public TokenRepository tokenRepository() {
        return new InMemoryTokenRepository();
    }
}
