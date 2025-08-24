package com.backend.easy_to_web.account.infrastructure.persistence.jpa;

import com.backend.easy_to_web.account.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, String> {
  boolean existsById(String id);
}
