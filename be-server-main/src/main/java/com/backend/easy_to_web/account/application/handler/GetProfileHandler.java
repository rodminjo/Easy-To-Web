package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.domain.result.GetProfileResult;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.NoUseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Handler
public class GetProfileHandler implements NoUseCaseHandler<GetProfileResult> {

  private final AccountRepository accountRepository;

  public GetProfileHandler(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }


  @Override
  public Class<GetProfileResult> getReturnClass() {return GetProfileResult.class;}

  @Override
  @Transactional(readOnly = true)
  public GetProfileResult handle() {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new CustomIllegalStateException(ExceptionMessage.LOGIN_INFO_INVALID));

    return GetProfileResult.of(account);

  }
}
