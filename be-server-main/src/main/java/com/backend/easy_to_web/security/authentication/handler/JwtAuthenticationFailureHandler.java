package com.backend.easy_to_web.security.authentication.handler;

import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.REFRESH_TOKEN_EXPIRED;
import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.REFRESH_TOKEN_INVALID;
import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.REFRESH_TOKEN_NOT_FOUND;
import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.USER_AGENT_INVALID;

import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.controller.BaseController;
import com.backend.easy_to_web.common.infrastructure.controller.rest.ErrorResponse;
import com.backend.easy_to_web.utils.MapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationFailureHandler extends BaseController implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    ExceptionMessage exceptionMessage;
    if (exception instanceof CustomAuthenticationException) {
      exceptionMessage = ((CustomAuthenticationException) exception).getExceptionMessage();
      if (
          exceptionMessage.equals(REFRESH_TOKEN_INVALID) ||
          exceptionMessage.equals(REFRESH_TOKEN_EXPIRED) ||
          exceptionMessage.equals(REFRESH_TOKEN_NOT_FOUND) ||
          exceptionMessage.equals(USER_AGENT_INVALID)
      ) {
        setEmptyRefreshToken(response);
      }

    } else {
      exceptionMessage = ExceptionMessage.AUTHENTICATED_ERROR;
      log.error("Authentication failed. Unexpected exception occurred: {}", exception.getMessage(), exception);
    }

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(exceptionMessage.getHttpStatus().value());
    MapperUtils.getMapper().writeValue(response.getWriter(), respond(new ErrorResponse(exceptionMessage)));
  }

  private void setEmptyRefreshToken(HttpServletResponse response){
    setRefreshToken(response, "", 0);
  }
}
