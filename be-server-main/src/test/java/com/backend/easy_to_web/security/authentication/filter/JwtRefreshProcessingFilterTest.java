package com.backend.easy_to_web.security.authentication.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.easy_to_web.common.application.port.ClockHolder;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.test_support.mock.properties.PropertiesContainer;
import com.backend.easy_to_web.test_support.mock.utils.MockClockHolder;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;


class JwtRefreshProcessingFilterTest extends UnitTest{

  private final PropertiesContainer propertiesContainer = new PropertiesContainer();

  private JwtManager getJwtManager() {
    SecurityProperties properties = propertiesContainer.getSecurityProperties();
    ClockHolder clockHolder = new MockClockHolder(new Date());
    return new JwtManager(properties, clockHolder);
  }

  @Test
  void 토큰이_존재하지_않는다면_오류를_출력한다() throws ServletException, IOException {
    // given
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();

    JwtRefreshProcessingFilter jwtRefreshProcessingFilter =
        new JwtRefreshProcessingFilter(authenticationManager, getJwtManager());

    // when
    assertThatThrownBy(() -> jwtRefreshProcessingFilter.attemptAuthentication(request, response))
        .isExactlyInstanceOf(CustomAuthenticationException.class)
        .isInstanceOf(RuntimeException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND.getDescription());

    // then
    verify(authenticationManager, times(0)).authenticate(any());
  }

  @Test
  @DisplayName("토큰이 존재하지만 User Agent가 없다면 오류를 출력한다")
  void throwExceptionIfUserAgentNotValid() throws ServletException, IOException {
    // given
    String refreshToken = "invalid_token";
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie(JwtManager.REFRESH_TOKEN, refreshToken));


    JwtRefreshProcessingFilter jwtRefreshProcessingFilter =
        new JwtRefreshProcessingFilter(authenticationManager, getJwtManager());

    // when
    assertThatThrownBy(() -> jwtRefreshProcessingFilter.attemptAuthentication(request, response))
        .isExactlyInstanceOf(CustomAuthenticationException.class)
        .isInstanceOf(RuntimeException.class)
        .hasMessage(ExceptionMessage.USER_AGENT_INVALID.getDescription());

    // then
    verify(authenticationManager, times(0)).authenticate(any());
  }


  @Test
  void 토큰이_존재하면_Provider에게_refreshToken을_전달한다() throws ServletException, IOException {
    // given
    String refreshToken = "invalid_token";
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie(JwtManager.REFRESH_TOKEN, refreshToken));
    request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");

    JwtRefreshProcessingFilter jwtRefreshProcessingFilter =
        new JwtRefreshProcessingFilter(authenticationManager, getJwtManager());

    // when
    jwtRefreshProcessingFilter.attemptAuthentication(request, response);

    // then
    ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
    verify(authenticationManager, times(1)).authenticate(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue().getPrincipal()).isEqualTo(refreshToken);
  }

}