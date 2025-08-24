package com.backend.easy_to_web.account.infrastructure.persistence;

import com.backend.easy_to_web.account.domain.model.AccountSNS;
import com.backend.easy_to_web.account.domain.model.SNSProvider;
import com.backend.easy_to_web.account.domain.port.out.AccountSNSRepository;
import com.backend.easy_to_web.account.infrastructure.persistence.entity.AccountSNSEntity;
import com.backend.easy_to_web.account.infrastructure.persistence.jpa.AccountSNSJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AccountSNSRepositoryAdapter implements AccountSNSRepository {

  private final AccountSNSJpaRepository accountSNSJpaRepository;


  @Override
  public Optional<AccountSNS> findByProviderAndProviderAccountId(SNSProvider provider,
      String providerAccountId) {
    return accountSNSJpaRepository.findByProviderAndProviderAccountId(provider, providerAccountId)
        .map(AccountSNSEntity::toDomain);
  }

  @Override
  public AccountSNS save(AccountSNS accountSNS) {
    return accountSNSJpaRepository.save(AccountSNSEntity.of(accountSNS))
        .toDomain();
  }

}
