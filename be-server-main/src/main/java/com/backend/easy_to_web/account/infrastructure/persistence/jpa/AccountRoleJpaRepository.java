package com.backend.easy_to_web.account.infrastructure.persistence.jpa;

import com.backend.easy_to_web.account.infrastructure.persistence.entity.AccountRoleEntity;
import com.backend.easy_to_web.account.infrastructure.persistence.entity.RoleEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRoleJpaRepository extends JpaRepository<AccountRoleEntity, Long> {

  @Query("""
        SELECT r FROM RoleEntity r
        JOIN AccountRoleEntity ar ON r.id = ar.roleId
        WHERE ar.accountId = :accountId
    """)
  List<RoleEntity> findAllRoleByAccountId(@Param("accountId") UUID accountId);

  List<AccountRoleEntity> findAllByAccountId(UUID accountId);

  void deleteByAccountIdAndRoleIdNotIn(UUID accountId, Collection<String> roleIds);
}
