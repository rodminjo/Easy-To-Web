package com.backend.easy_to_web.test_support.mock.account.application;

import com.backend.easy_to_web.account.domain.result.VerifyMailCertificationResult;
import com.backend.easy_to_web.account.domain.usecase.VerifyMailCertificationUseCase;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeMailCertificationHandler implements UseCaseHandler<VerifyMailCertificationResult, VerifyMailCertificationUseCase> {

  private final String invalidEmail;


  @Override
  public Class<VerifyMailCertificationUseCase> getUseCaseClass() {
    return VerifyMailCertificationUseCase.class;
  }

  @Override
  public VerifyMailCertificationResult handle(VerifyMailCertificationUseCase useCase) {
    return new VerifyMailCertificationResult(!useCase.getEmail().equals(invalidEmail));
  }
}
