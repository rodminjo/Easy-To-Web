package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.domain.usecase.UpdateProfileUseCase;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Handler
public class UpdateProfileHandler implements VoidUseCaseHandler<UpdateProfileUseCase> {

  private final AccountRepository accountRepository;

  public UpdateProfileHandler(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }


  @Override
  public Class<UpdateProfileUseCase> getVoidUseCaseClass() {
    return UpdateProfileUseCase.class;
  }

  @Override
  @Transactional
  public void handleVoid(UpdateProfileUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new CustomIllegalStateException(ExceptionMessage.LOGIN_INFO_INVALID));

    account.update(useCase.getNickname(), useCase.getProfileUrl());
    accountRepository.save(account);
  }
}
