package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.model.AccountSNS;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.domain.port.out.AccountSNSRepository;
import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.account.domain.usecase.LoginOrRegisterAccountUseCase;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class LoginOrRegisterAccountHandler implements UseCaseHandler<LoginResult, LoginOrRegisterAccountUseCase> {

  private final AccountSNSRepository accountSNSRepository;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Class<LoginOrRegisterAccountUseCase> getUseCaseClass() {
    return LoginOrRegisterAccountUseCase.class;
  }

  @Override
  @Transactional
  public LoginResult handle(LoginOrRegisterAccountUseCase useCase) {
    Optional<AccountSNS> snsInfo = accountSNSRepository.findByProviderAndProviderAccountId(useCase.getProvider(), useCase.getNameAttribute());
    if (snsInfo.isEmpty()){
      Optional<Account> findByEmail = accountRepository.findByEmail(useCase.getEmail());

      Account account = findByEmail.orElseGet(() -> {
        Account newAccount = useCase.createAccount(passwordEncoder);
        return accountRepository.save(newAccount);
      });

      AccountSNS accountSNS = useCase.createAccountSNS(account);
      accountSNSRepository.save(accountSNS);

      return LoginResult.of(account);

    } else {
      AccountSNS accountSNS = snsInfo.get();
      Account find = accountRepository.getById(accountSNS.getAccountId());
      return LoginResult.of(find);

    }
  }
}
