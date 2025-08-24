package com.backend.easy_to_web.security.authentication.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.backend.easy_to_web.common.domain.exception.CustomAccessDeniedException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;


class JwtAccessDeniedHandlerTest extends UnitTest{

  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();
  private final JwtAccessDeniedHandler handler = new JwtAccessDeniedHandler();

  @Test
  @DisplayName("CustomAccessDeniedException 예외일 경우 해당 예외 메세지를 담는다")
  void shouldContainCustomExceptionMessageInResponse() throws ServletException, IOException {
    // when
    handler.handle(request, response, new CustomAccessDeniedException(ExceptionMessage.ACCESS_DENIED));
    String responseBody = response.getContentAsString();

    // then
    assertThat(responseBody).contains(ExceptionMessage.ACCESS_DENIED.getErrorCode());
    assertThat(responseBody).contains(ExceptionMessage.ACCESS_DENIED.getDescription());
    assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
  }

  @Test
  @DisplayName("예상하지 못한 예외일 경우 해당 AccessError 메세지를 담는다")
  void shouldContainUnexpectExceptionMessageInResponse() throws ServletException, IOException {
    // when
    String unexpectMsg = "Unexpect exception";
    handler.handle(request, response, new AccessDeniedException(unexpectMsg));
    String responseBody = response.getContentAsString();

    // then
    assertThat(responseBody).contains(ExceptionMessage.ACCESS_ERROR.getErrorCode());
    assertThat(responseBody).contains(ExceptionMessage.ACCESS_ERROR.getDescription());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }
}