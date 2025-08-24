package com.backend.easy_to_web.account.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.easy_to_web.account.domain.model.RefreshToken;
import com.backend.easy_to_web.account.domain.port.out.RefreshTokenRepository;
import com.backend.easy_to_web.account.domain.usecase.CheckRefreshTokenUseCase;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeRefreshTokenRepository;
import com.backend.easy_to_web.test_support.model.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CheckRefreshTokenHandlerTest extends UnitTest{

  private final String accountId = "3578e885-f55b-4476-925c-5838b58f4607";
  private final String refreshToken = "test-refresh-token";
  private final String userIp = "127.0.0.1";
  private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36";
  CheckRefreshTokenUseCase command = new CheckRefreshTokenUseCase(accountId, refreshToken);

  @Test
  @DisplayName("토큰이 존재하지 않을 경우 CustomAuthenticationException 예외를 반환한다")
  void shouldThrowExceptionWhenRefreshTokenNotFound() {
    // given
    RefreshTokenRepository refreshTokenRepository = new FakeRefreshTokenRepository();
    CheckRefreshTokenHandler handler = new CheckRefreshTokenHandler(refreshTokenRepository);

    // when & then
    assertThatThrownBy(() -> handler.handle(command))
        .isExactlyInstanceOf(CustomAuthenticationException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_INVALID.getDescription());
  }

  @Test
  @DisplayName("토큰이 존재하면 토큰을 반환한다")
  void shouldReturnTokenWhenSucceeded(){
    // given
    RefreshTokenRepository refreshTokenRepository = new FakeRefreshTokenRepository();
    RefreshToken token = RefreshToken.builder()
        .accountId(accountId)
        .token(refreshToken)
        .userIp(userIp)
        .userAgent(userAgent)
        .build();

    refreshTokenRepository.save(token);
    CheckRefreshTokenHandler handler = new CheckRefreshTokenHandler(refreshTokenRepository);

    // when & then
    String findToken = handler.handle(command);
    assertThat(findToken).isEqualTo(refreshToken);
  }
}

