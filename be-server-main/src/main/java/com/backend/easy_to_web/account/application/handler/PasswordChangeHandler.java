package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.usecase.PasswordChangeUseCase;
import com.backend.easy_to_web.account.domain.usecase.VerifyMailCertificationUseCase;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class PasswordChangeHandler implements VoidUseCaseHandler<PasswordChangeUseCase> {

  private final CertificationMailService certificationMailService;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Class<PasswordChangeUseCase> getVoidUseCaseClass() {
    return PasswordChangeUseCase.class;
  }

  @Override
  @Transactional
  public void handleVoid(PasswordChangeUseCase useCase) {
    VerifyMailCertificationUseCase certificationCommand = new VerifyMailCertificationUseCase(
        useCase.getType(),
        useCase.getEmail(),
        useCase.getCertificationCode()
    );
    // 인증 확인
    boolean isValid = certificationMailService.isValidCertification(useCase.getType(),
        useCase.getEmail(), useCase.getCertificationCode()
    );

    if (!isValid){
      throw new CustomIllegalArgumentException(ExceptionMessage.CERTIFICATION_FAILED);
    }

    // 이메일 존재하는지 확인
    Account account = accountRepository.findByEmail(certificationCommand.getEmail())
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.EMAIL_NOT_FOUND));

    // 계정 비밀번호 변경
    account.changePassword(useCase.getPassword(), passwordEncoder);
    accountRepository.save(account);
  }

}
