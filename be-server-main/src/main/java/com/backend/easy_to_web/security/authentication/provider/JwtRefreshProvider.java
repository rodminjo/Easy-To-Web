package com.backend.easy_to_web.security.authentication.provider;

import com.backend.easy_to_web.account.domain.usecase.CheckRefreshTokenUseCase;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.token.AccountToken;
import com.backend.easy_to_web.security.authentication.token.JwtRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtRefreshProvider implements AuthenticationProvider {

  private final JwtManager jwtManager;
  private final UseCasePublisher useCasePublisher;

  // JwtRefreshToken 형식의 Authentication 객체를 받는다.
  // RefreshProcessingFilter 에서 Principal 에 refreshToken 을 넣어 놓음
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    // 계정 정보 찾기
    String refreshToken = (String) authentication.getPrincipal();
    boolean isValid = jwtManager.validateToken(refreshToken);
    if (!isValid){
      throw new CustomAuthenticationException(ExceptionMessage.REFRESH_TOKEN_EXPIRED);
    }

    // DB 에서 refresh token을 가져오고 문제가 없다면 저장
    String id = jwtManager.getAuthentication(refreshToken).getPrincipal().toString();
    CheckRefreshTokenUseCase command = new CheckRefreshTokenUseCase(id, refreshToken);
    String findToken = useCasePublisher.publish(String.class, command);

    return new AccountToken(
        id,
        null,
        null,
        authentication.getAuthorities()
    );
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(JwtRefreshToken.class);
  }


}
