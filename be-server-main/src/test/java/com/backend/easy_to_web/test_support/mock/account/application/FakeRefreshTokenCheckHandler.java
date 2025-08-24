package com.backend.easy_to_web.test_support.mock.account.application;

import com.backend.easy_to_web.account.domain.usecase.CheckRefreshTokenUseCase;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;

public class FakeRefreshTokenCheckHandler implements UseCaseHandler<String, CheckRefreshTokenUseCase> {

  @Override
  public Class<CheckRefreshTokenUseCase> getUseCaseClass() {
    return CheckRefreshTokenUseCase.class;
  }

  @Override
  public String handle(CheckRefreshTokenUseCase useCase) {
    return useCase.getRefreshToken();
  }
}
