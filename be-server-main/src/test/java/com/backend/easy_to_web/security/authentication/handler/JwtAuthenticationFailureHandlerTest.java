package com.backend.easy_to_web.security.authentication.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



@TestInstance(value = Lifecycle.PER_CLASS)
class JwtAuthenticationFailureHandlerTest extends UnitTest{

  private final JwtAuthenticationFailureHandler handler = new JwtAuthenticationFailureHandler();

  @BeforeAll
  void setup() {
  }

  @Test
  @DisplayName("CustomAuthenticationException 예외일 경우 해당 예외 메세지를 담는다")
  void shouldContainCustomExceptionMessageInResponse() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // when
    handler.onAuthenticationFailure(request, response, new CustomAuthenticationException(ExceptionMessage.LOGIN_INFO_INVALID));
    String responseBody = response.getContentAsString();

    // then
    assertThat(responseBody).contains(ExceptionMessage.LOGIN_INFO_INVALID.getErrorCode());
    assertThat(responseBody).contains(ExceptionMessage.LOGIN_INFO_INVALID.getDescription());
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  @DisplayName("예상하지 못한 예외일 경우 해당 예외 메세지를 담는다")
  void shouldContainUnexpectExceptionMessageInResponse() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // when
    handler.onAuthenticationFailure(request, response, new UsernameNotFoundException(ExceptionMessage.AUTHENTICATED_ERROR.getDescription()));
    String responseBody = response.getContentAsString();

    // then
    assertThat(responseBody).contains(ExceptionMessage.AUTHENTICATED_ERROR.getErrorCode());
    assertThat(responseBody).contains(ExceptionMessage.AUTHENTICATED_ERROR.getDescription());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

}