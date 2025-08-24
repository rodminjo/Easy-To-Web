package com.backend.easy_to_web.security.authentication.handler;

import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    ResponseCookie refreshTokenCookie = ResponseCookie.from(JwtManager.REFRESH_TOKEN, "")
        .maxAge(0) // 쿠키의 만료 시간 설정 (초 단위)
        .httpOnly(true) // JavaScript에서 쿠키 접근 불가 설정
        .path("/api/account/reissue") // 쿠키의 유효 범위 설정
        .build();

    response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.OK.value());
    response.getWriter().write("{\"data\": \"로그아웃 되었습니다.\"}");
  }
}
