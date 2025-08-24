package com.backend.easy_to_web.test_support.mock.account.application;

import com.backend.easy_to_web.account.domain.usecase.MergeRefreshTokenUseCase;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;

public class FakeRefreshTokenMergeHandler implements UseCaseHandler<String, MergeRefreshTokenUseCase> {


  @Override
  public Class<MergeRefreshTokenUseCase> getUseCaseClass() {
    return MergeRefreshTokenUseCase.class;
  }

  @Override
  public String handle(MergeRefreshTokenUseCase useCase) {
    return useCase.getRefreshToken();
  }
}
