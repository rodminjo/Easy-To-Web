package com.backend.easy_to_web.config;

import com.backend.easy_to_web.config.dsl.JwtLoginDsl;
import com.backend.easy_to_web.config.dsl.JwtRefreshDsl;
import com.backend.easy_to_web.config.properties.ProjectSocketProperties;
import com.backend.easy_to_web.security.authentication.filter.JwtAuthenticationFilter;
import com.backend.easy_to_web.security.authentication.handler.JwtAccessDeniedHandler;
import com.backend.easy_to_web.security.authentication.handler.JwtAuthenticationEntryPoint;
import com.backend.easy_to_web.security.authentication.handler.JwtAuthenticationFailureHandler;
import com.backend.easy_to_web.security.authentication.handler.JwtAuthenticationSuccessHandler;
import com.backend.easy_to_web.security.authentication.handler.JwtLogoutSuccessHandler;
import com.backend.easy_to_web.security.authentication.handler.OAuth2AuthenticationFailureHandler;
import com.backend.easy_to_web.security.authentication.handler.OAuth2AuthenticationSuccessHandler;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.provider.JwtAuthenticationProvider;
import com.backend.easy_to_web.security.authentication.provider.JwtRefreshProvider;
import com.backend.easy_to_web.security.authentication.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationProvider jwtAuthenticationProvider;
  private final JwtRefreshProvider jwtRefreshProvider;

  private final JwtManager jwtManager;

  private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;
  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

  private final OAuth2AuthenticationSuccessHandler oAuthSuccessHandler;
  private final OAuth2AuthenticationFailureHandler oAuthFailureHandler;
  private final HttpCookieOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

  private final ProjectSocketProperties projectSocketProperties;


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests((auth) -> auth
            // resources 전부 허용
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers(
                "/api",
                "/api/account/**",
                "/api/file/{fileId}",
                "/swagger/**",
                "/api/swagger-config"
            ).permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/project/publish/**").permitAll()
            .requestMatchers(projectSocketProperties.getPath()).permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .exceptionHandling((exception) -> exception
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
        )
        .oauth2Login(oauth -> oauth
            .userInfoEndpoint(Customizer.withDefaults())
            .authorizationEndpoint(a -> a
                .authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
            )
            .successHandler(oAuthSuccessHandler)
            .failureHandler(oAuthFailureHandler)
        )
        .authenticationProvider(jwtAuthenticationProvider)
        .authenticationProvider(jwtRefreshProvider)
        .with(
            new JwtRefreshDsl(jwtAuthenticationSuccessHandler, jwtAuthenticationFailureHandler, jwtManager),
            jwtRefreshDsl -> {}
        )
        .with(
            new JwtLoginDsl(jwtAuthenticationSuccessHandler, jwtAuthenticationFailureHandler),
            jwtLoginDsl -> {}
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(logout -> logout
            .logoutUrl("/api/account/logout")
            .logoutSuccessHandler(jwtLogoutSuccessHandler)
        )
    ;

    return http.build();
  }
}
