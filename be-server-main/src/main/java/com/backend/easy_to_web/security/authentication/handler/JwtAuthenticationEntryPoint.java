package com.backend.easy_to_web.security.authentication.handler;

import com.backend.easy_to_web.common.infrastructure.controller.rest.ErrorResponse;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.manager.JwtManager.JwtStatus;
import com.backend.easy_to_web.utils.MapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 인증되지 않은 사용자가 인증이 필요한 요청 엔드포인트로 접근하려 할 때, 예외 핸들링
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final JwtManager jwtManager;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    String accessToken = jwtManager.resolveToken(request);
    JwtStatus status = jwtManager.statusToken(accessToken);

    ExceptionMessage exceptionMessage = status.equals(JwtStatus.EXPIRED) ?
        ExceptionMessage.ACCESS_TOKEN_EXPIRED :
        ExceptionMessage.USER_NOT_LOGIN;

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(exceptionMessage.getHttpStatus().value());
    response.getWriter().write(MapperUtils.getMapper().writeValueAsString(new ErrorResponse(exceptionMessage)));
  }
}
