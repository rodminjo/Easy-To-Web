package com.backend.easy_to_web.security.authentication.handler;

import com.backend.easy_to_web.common.infrastructure.controller.rest.ErrorResponse;
import com.backend.easy_to_web.common.domain.exception.CustomAccessDeniedException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.utils.MapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


/**
 * 인가되지 않은 사용자가 인가가 필요한 요청 엔드포인트로 접근하려 할 때, 예외 핸들링
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    ExceptionMessage exceptionMessage;
    if (accessDeniedException instanceof CustomAccessDeniedException){
      exceptionMessage = ((CustomAccessDeniedException) accessDeniedException).getExceptionMessage();
    } else {
      exceptionMessage = ExceptionMessage.ACCESS_ERROR;
      log.error("Access denied. Exception occurred: {}", accessDeniedException.getMessage(), accessDeniedException);
    }

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(exceptionMessage.getHttpStatus().value());
    response.getWriter().write(MapperUtils.getMapper().writeValueAsString(new ErrorResponse(exceptionMessage)));
  }
}
