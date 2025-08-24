package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.domain.result.JoinAccountResult;
import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.usecase.JoinAccountUseCase;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class JoinAccountHandler implements UseCaseHandler<JoinAccountResult, JoinAccountUseCase> {

  private final CertificationMailService certificationMailService;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;


  @Override
  @Transactional
  public JoinAccountResult handle(JoinAccountUseCase useCase) {
    // 인증 확인
    boolean isValid = certificationMailService.isValidCertification(useCase.getType(),
        useCase.getEmail(), useCase.getCertificationCode()
    );

    if (!isValid){
      throw new CustomIllegalArgumentException(ExceptionMessage.CERTIFICATION_FAILED);
    }

    // 이메일 중복 확인
    if (accountRepository.findByEmail(useCase.getEmail()).isPresent()){
      throw new CustomIllegalArgumentException(ExceptionMessage.EMAIL_ALREADY_EXISTS);
    }

    // 계정 생성
    Account account = useCase.createAccount(passwordEncoder);
    Account savedAccount = accountRepository.save(account);

    // 결과 반환
    return new JoinAccountResult(savedAccount.getEmail());
  }

  @Override
  public Class<JoinAccountUseCase> getUseCaseClass() {
    return JoinAccountUseCase.class;
  }
}
