package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.result.SendJoinMailResult;
import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.usecase.SendJoinMailUseCase;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.infrastructure.external.JoinCertificationEmailSender;
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
public class JoinMailSendHandler implements UseCaseHandler<SendJoinMailResult, SendJoinMailUseCase> {

  public static final String EMAIL_PREFIX = "VERIFIED_EMAIL";

  private final AccountRepository accountRepository;
  private final CertificationMailService certificationMailService;
  private final EmailSender emailSender;


  public JoinMailSendHandler(
      AccountRepository accountRepository,
      CertificationMailService certificationMailService,
      @Qualifier("joinCertificationEmailSender") EmailSender emailSender
  ) {
    this.accountRepository = accountRepository;
    this.certificationMailService = certificationMailService;
    this.emailSender = emailSender;
  }

  @Override
  public Class<SendJoinMailUseCase> getUseCaseClass() {
    return SendJoinMailUseCase.class;
  }

  @Override
  public SendJoinMailResult handle(SendJoinMailUseCase useCase) {
    validateEmailDuplication(useCase.getEmail());
    certificationMailService.sendAndSaveCertificationCode(EMAIL_PREFIX, useCase.getEmail(), emailSender);

    return new SendJoinMailResult(EMAIL_PREFIX);
  }

  // 이메일 중복 체크
  private void validateEmailDuplication(String email) {
    if (accountRepository.findByEmail(email).isPresent()) {
      throw new CustomIllegalArgumentException(ExceptionMessage.EMAIL_ALREADY_EXISTS);
    }
  }



}
