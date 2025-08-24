package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.account.domain.usecase.LoginUseCase;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import org.springframework.transaction.annotation.Transactional;

@Handler
public class LoginHandler implements UseCaseHandler<LoginResult, LoginUseCase> {

  private final AccountRepository accountRepository;

  public LoginHandler(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public Class<LoginUseCase> getUseCaseClass() {
    return LoginUseCase.class;
  }

  @Override
  @Transactional(readOnly = true)
  public LoginResult handle(LoginUseCase useCase) {
    // 계정 정보 찾기
    Account account = accountRepository.findByEmail(useCase.getEmail())
        .orElseThrow(() -> new CustomAuthenticationException(ExceptionMessage.LOGIN_INFO_INVALID));

    return LoginResult.of(account);
  }
}
