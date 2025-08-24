package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.port.out.RefreshTokenRepository;
import com.backend.easy_to_web.account.domain.usecase.CheckRefreshTokenUseCase;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class CheckRefreshTokenHandler implements UseCaseHandler<String, CheckRefreshTokenUseCase> {

  private final RefreshTokenRepository refreshTokenRepository;


  @Override
  public Class<CheckRefreshTokenUseCase> getUseCaseClass() {
    return CheckRefreshTokenUseCase.class;
  }

  @Override
  @Transactional(readOnly = true)
  public String handle(CheckRefreshTokenUseCase useCase) {
    return refreshTokenRepository.findAllByAccountId(useCase.getAccountId()).stream()
        .filter(token -> token.getToken().equals(useCase.getRefreshToken()))
        .findFirst()
        .orElseThrow(() -> new CustomAuthenticationException(ExceptionMessage.REFRESH_TOKEN_INVALID))
        .getToken();
  }
}
