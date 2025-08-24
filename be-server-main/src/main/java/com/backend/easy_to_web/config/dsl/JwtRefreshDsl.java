package com.backend.easy_to_web.config.dsl;

import com.backend.easy_to_web.security.authentication.filter.JwtAuthenticationFilter;
import com.backend.easy_to_web.security.authentication.filter.JwtRefreshProcessingFilter;
import com.backend.easy_to_web.security.authentication.handler.JwtAuthenticationFailureHandler;
import com.backend.easy_to_web.security.authentication.handler.JwtAuthenticationSuccessHandler;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class JwtRefreshDsl extends AbstractHttpConfigurer<JwtRefreshDsl, HttpSecurity> {

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
  private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;
  private final JwtManager jwtManager;
  @Override
  public void configure(HttpSecurity http) throws Exception {
    AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
    JwtRefreshProcessingFilter jwtRefreshProcessingFilter =
        new JwtRefreshProcessingFilter(authenticationManager, jwtManager);
    jwtRefreshProcessingFilter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
    jwtRefreshProcessingFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
    http.addFilterBefore(jwtRefreshProcessingFilter, JwtAuthenticationFilter.class);
  }
}