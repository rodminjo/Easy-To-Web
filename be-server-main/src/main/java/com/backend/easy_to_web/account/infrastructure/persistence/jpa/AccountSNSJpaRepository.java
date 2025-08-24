package com.backend.easy_to_web.account.infrastructure.persistence.jpa;

import com.backend.easy_to_web.account.domain.model.SNSProvider;
import com.backend.easy_to_web.account.infrastructure.persistence.entity.AccountSNSEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSNSJpaRepository extends JpaRepository<AccountSNSEntity, Long> {

  Optional<AccountSNSEntity> findByProviderAndProviderAccountId(SNSProvider provider, String providerAccountId);

}
