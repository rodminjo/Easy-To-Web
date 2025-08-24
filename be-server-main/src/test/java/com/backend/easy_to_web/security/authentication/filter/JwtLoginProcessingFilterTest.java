package com.backend.easy_to_web.security.authentication.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.token.JwtLoginToken;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;


class JwtLoginProcessingFilterTest extends UnitTest{

  @Test
  @DisplayName("request에 로그인 정보가 존재하지 않으면 오류가발생한다")
  void loginInfoNotFoundTest (){
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    JwtLoginProcessingFilter jwtLoginProcessingFilter =
        new JwtLoginProcessingFilter(authenticationManager);

    // when & then
    assertThatThrownBy(() -> jwtLoginProcessingFilter.attemptAuthentication(request, response))
        .isExactlyInstanceOf(CustomAuthenticationException.class)
        .isInstanceOf(RuntimeException.class)
        .hasMessage(ExceptionMessage.LOGIN_INFO_NOT_FOUND.getDescription());
  }

  @Test
  @DisplayName("request에 로그인 정보가 존재하면 토큰을 생성하고 provider에게 넘겨준다")
  void loginInfoFoundTest() throws ServletException, IOException {
    // given
    String email = "test@test.com";
    String password = "testPassword";
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    String jsonContent = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";
    request.setContent(jsonContent.getBytes(StandardCharsets.UTF_8));

    JwtLoginProcessingFilter jwtLoginProcessingFilter =
        new JwtLoginProcessingFilter(authenticationManager);

    // when
    jwtLoginProcessingFilter.attemptAuthentication(request, response);

    // then
    ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
    verify(authenticationManager, times(1)).authenticate(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isExactlyInstanceOf(JwtLoginToken.class);
    assertThat(argumentCaptor.getValue().getPrincipal()).isEqualTo(email);
    assertThat(argumentCaptor.getValue().getCredentials()).isEqualTo(password);

  }
}