package com.backend.easy_to_web.security.authentication.handler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.backend.easy_to_web.common.application.port.ClockHolder;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.test_support.mock.utils.MockClockHolder;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


@TestInstance(value = Lifecycle.PER_CLASS)
class JwtAuthenticationEntryPointTest extends UnitTest{

  private JwtManager jwtManager;
  private JwtAuthenticationEntryPoint entryPoint;

  @BeforeAll
  void setup() {
    long expiredTime = 1L;
    String testSecretKey = "123412341234123412341234123412342374ijdskfg.";

    SecurityProperties properties = new SecurityProperties(testSecretKey, expiredTime, expiredTime);
    ClockHolder clockHolder = new MockClockHolder(new Date());
    jwtManager = new JwtManager(properties, clockHolder);

    entryPoint = new JwtAuthenticationEntryPoint(jwtManager);
  }

  @Test
  @DisplayName("Access Token이 만료된 경우 적절한 예외 메시지를 반환한다")
  void shouldReturnExpiredTokenMessageWhenAccessTokenIsExpired() throws IOException, ServletException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    String expiredToken = jwtManager.generateToken("test@example.com", Collections.emptyList(), true);
    request.addHeader(JwtManager.AUTHORIZATION, JwtManager.BEARER + " " + expiredToken);

    // when
    entryPoint.commence(request, response, new CustomAuthenticationException(ExceptionMessage.USER_NOT_LOGIN));

    // then
    assertThat(response.getStatus()).isEqualTo(ExceptionMessage.ACCESS_TOKEN_EXPIRED.getHttpStatus().value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    String responseBody = response.getContentAsString();
    assertThat(responseBody).contains(ExceptionMessage.ACCESS_TOKEN_EXPIRED.getDescription());
  }

  @Test
  @DisplayName("Access Token이 없거나 유효하지 않을 경우 적절한 예외 메시지를 반환한다")
  void shouldReturnNotLoggedInMessageWhenAccessTokenIsInvalidOrMissing() throws IOException, ServletException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader(JwtManager.AUTHORIZATION, JwtManager.BEARER + " invalid_token");

    // when
    entryPoint.commence(request, response, new CustomAuthenticationException(ExceptionMessage.USER_NOT_LOGIN));

    // then
    assertThat(response.getStatus()).isEqualTo(ExceptionMessage.USER_NOT_LOGIN.getHttpStatus().value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    String responseBody = response.getContentAsString();
    assertThat(responseBody).contains(ExceptionMessage.USER_NOT_LOGIN.getDescription());
  }
}
