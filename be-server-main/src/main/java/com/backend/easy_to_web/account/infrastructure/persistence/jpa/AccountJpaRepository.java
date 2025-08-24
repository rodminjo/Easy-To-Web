package com.backend.easy_to_web.account.infrastructure.persistence.jpa;

import com.backend.easy_to_web.account.infrastructure.persistence.entity.AccountEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

  Optional<AccountEntity> findByEmail(String email);

}
