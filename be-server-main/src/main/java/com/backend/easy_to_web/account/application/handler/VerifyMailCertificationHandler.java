package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.result.VerifyMailCertificationResult;
import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.usecase.VerifyMailCertificationUseCase;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Handler
public class VerifyMailCertificationHandler implements UseCaseHandler<VerifyMailCertificationResult, VerifyMailCertificationUseCase> {

  private final CertificationMailService certificationMailService;


  @Override
  public Class<VerifyMailCertificationUseCase> getUseCaseClass() {
    return VerifyMailCertificationUseCase.class;
  }

  @Override
  public VerifyMailCertificationResult handle(VerifyMailCertificationUseCase useCase) {
    boolean isValid = certificationMailService.isValidCertification(useCase.getType(),
        useCase.getEmail(), useCase.getCertificationCode()
    );

    return new VerifyMailCertificationResult(isValid);
  }
}
