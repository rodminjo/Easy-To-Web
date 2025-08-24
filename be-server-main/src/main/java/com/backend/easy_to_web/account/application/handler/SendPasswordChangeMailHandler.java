package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.result.SendPasswordChangeMailResult;
import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.usecase.SendPasswordChangeMailUseCase;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.infrastructure.external.PasswordCertificationEmailSender;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.application.port.EmailSender;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@Handler
public class SendPasswordChangeMailHandler implements UseCaseHandler<SendPasswordChangeMailResult, SendPasswordChangeMailUseCase> {

  public static final String EMAIL_PREFIX = "PASSWORD_CHANGE_EMAIL";

  private final AccountRepository accountRepository;
  private final CertificationMailService certificationMailSendService;
  private final EmailSender emailSender;

  public SendPasswordChangeMailHandler(
      AccountRepository accountRepository,
      CertificationMailService certificationMailSendService,
      @Qualifier("passwordCertificationEmailSender") EmailSender emailSender
  ) {
    this.accountRepository = accountRepository;
    this.certificationMailSendService = certificationMailSendService;
    this.emailSender = emailSender;
  }

  @Override
  public Class<SendPasswordChangeMailUseCase> getUseCaseClass() {
    return SendPasswordChangeMailUseCase.class;
  }

  @Override
  public SendPasswordChangeMailResult handle(SendPasswordChangeMailUseCase useCase) {
    validateEmailExists(useCase);
    certificationMailSendService.sendAndSaveCertificationCode(EMAIL_PREFIX, useCase.getEmail(), emailSender);

    return new SendPasswordChangeMailResult(EMAIL_PREFIX);
  }


  // 이메일이 존재하는지 체크
  private void validateEmailExists(SendPasswordChangeMailUseCase useCase) {
    if (accountRepository.findByEmail(useCase.getEmail()).isEmpty()) {
      throw new CustomIllegalArgumentException(ExceptionMessage.EMAIL_NOT_FOUND);
    }
  }
}
