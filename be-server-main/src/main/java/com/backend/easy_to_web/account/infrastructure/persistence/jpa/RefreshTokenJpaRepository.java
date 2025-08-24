package com.backend.easy_to_web.account.infrastructure.persistence.jpa;

import com.backend.easy_to_web.account.infrastructure.persistence.entity.RefreshTokenEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

  List<RefreshTokenEntity> findAllByAccountId(String accountId);

}