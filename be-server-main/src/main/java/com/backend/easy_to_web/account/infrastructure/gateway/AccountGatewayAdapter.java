package com.backend.easy_to_web.account.infrastructure.gateway;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.in.AccountGateway;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AccountGatewayAdapter implements AccountGateway {

  private final AccountRepository accountRepository;

  @Override
  public List<Account> findAllByIds(List<UUID> ids) {
    return accountRepository.findAllByIds(ids);
  }

  @Override
  public Account getByEmail(String email) {
    return accountRepository.getByEmail(email);
  }
}
