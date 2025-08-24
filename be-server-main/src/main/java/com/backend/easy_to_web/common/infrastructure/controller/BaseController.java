package com.backend.easy_to_web.common.infrastructure.controller;

import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.DataResponse;
import com.backend.easy_to_web.common.infrastructure.controller.rest.ErrorResponse;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.common.infrastructure.controller.rest.ResponseBuilder;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

public class BaseController {
  private  <T> Response<DataResponse<T>> respond(List<T> items) {
    return ResponseBuilder.build(items);
  }

  protected  <T> Response<DataResponse<T>> respond(List<T> items, int page, int size, Long totalSize) {
    return ResponseBuilder.build(items, page, size, totalSize);
  }

  protected <T> Response<T> respond(T item) {
    return ResponseBuilder.build(item);
  }

  protected Response<ErrorResponse> respond(ErrorResponse errorResponse) {
    return ResponseBuilder.build(errorResponse);
  }

  protected Response<Void> voidRespond() {
    return ResponseBuilder.build((Void) null);
  }

  protected ResponseEntity<Response<ErrorResponse>> exceptionRespondEntity(ExceptionMessage exceptionMessage) {
    Response<ErrorResponse> body = ResponseBuilder.build(new ErrorResponse(exceptionMessage));
    return ResponseEntity.status(exceptionMessage.getHttpStatus())
        .body(body);
  }

  protected ResponseEntity<Response<ErrorResponse>> exceptionRespondEntity(ExceptionMessage exceptionMessage, String errorDescription) {
    Response<ErrorResponse> body = ResponseBuilder.build(new ErrorResponse(exceptionMessage.getErrorCode(), errorDescription));
    return ResponseEntity.status(exceptionMessage.getHttpStatus())
        .body(body);
  }

  protected void setRefreshToken(HttpServletResponse response, String refreshToken, long expireTime){
    // refresh token cookie
    ResponseCookie refreshTokenCookie = ResponseCookie.from(JwtManager.REFRESH_TOKEN, refreshToken)
        .maxAge(expireTime / 1000) // 쿠키의 만료 시간 설정 (초 단위)
        .httpOnly(true) // JavaScript에서 쿠키 접근 불가 설정
        .path("/api/account/reissue") // 쿠키의 유효 범위 설정
        .build();

    response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
  }
}
