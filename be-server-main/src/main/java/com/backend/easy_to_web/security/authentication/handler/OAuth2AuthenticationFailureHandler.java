package com.backend.easy_to_web.security.authentication.handler;

import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.REFRESH_TOKEN_EXPIRED;
import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.REFRESH_TOKEN_INVALID;
import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.REFRESH_TOKEN_NOT_FOUND;
import static com.backend.easy_to_web.common.domain.exception.ExceptionMessage.USER_AGENT_INVALID;

import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.controller.BaseController;
import com.backend.easy_to_web.config.properties.ServerProperties;
import com.backend.easy_to_web.security.authentication.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.backend.easy_to_web.utils.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends BaseController implements AuthenticationFailureHandler {

  private final ServerProperties serverProperties;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    ExceptionMessage exceptionMessage;

    if (exception instanceof CustomAuthenticationException cae) {
      exceptionMessage = cae.getExceptionMessage();
      if (exceptionMessage.equals(REFRESH_TOKEN_INVALID)
          || exceptionMessage.equals(REFRESH_TOKEN_EXPIRED)
          || exceptionMessage.equals(REFRESH_TOKEN_NOT_FOUND)
          || exceptionMessage.equals(USER_AGENT_INVALID)) {
      }
    }
    // OAuth2 인증 단계에서 발생하는 표준 예외 매핑
    else if (exception instanceof OAuth2AuthenticationException oae) {
      log.warn("OAuth2 auth failed. code={}, desc={}",
          oae.getError().getErrorCode(), oae.getError().getDescription());

      exceptionMessage = ExceptionMessage.OAUTH_INVALID;
    } else {
      exceptionMessage = ExceptionMessage.AUTHENTICATED_ERROR;
      log.error("Authentication failed. Unexpected exception occurred: {}", exception.getMessage(), exception);
    }

    setEmptyRefreshToken(response);
    String next = CookieUtils.getCookieValue(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME).orElse("/");
    if (!next.startsWith("/")){
      next = "/" + next;
    }

    response.sendRedirect(serverProperties.getFeDomain() + next + "?oAuthError=" + exceptionMessage.getDescription());
  }

  private void setEmptyRefreshToken(HttpServletResponse response) {
    setRefreshToken(response, "", 0);
  }
}
