package com.backend.easy_to_web.config.dsl;

import com.backend.easy_to_web.security.authentication.filter.JwtAuthenticationFilter;
import com.backend.easy_to_web.security.authentication.filter.JwtLoginProcessingFilter;
import com.backend.easy_to_web.security.authentication.handler.JwtAuthenticationFailureHandler;
import com.backend.easy_to_web.security.authentication.handler.JwtAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class JwtLoginDsl extends AbstractHttpConfigurer<JwtLoginDsl, HttpSecurity> {

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
  private final JwtAuthenticationFailureHandler jotAuthenticationFailureHandler;
  @Override
  public void configure(HttpSecurity http) throws Exception {
    AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
    JwtLoginProcessingFilter jwtLoginProcessingFilter = new JwtLoginProcessingFilter(authenticationManager);
    jwtLoginProcessingFilter.setAuthenticationFailureHandler(jotAuthenticationFailureHandler);
    jwtLoginProcessingFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
    http.addFilterBefore(jwtLoginProcessingFilter, JwtAuthenticationFilter.class);
  }
}