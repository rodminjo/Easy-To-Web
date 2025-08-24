package com.backend.easy_to_web.account.infrastructure.persistence;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.model.Role;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.infrastructure.persistence.entity.AccountEntity;
import com.backend.easy_to_web.account.infrastructure.persistence.entity.AccountRoleEntity;
import com.backend.easy_to_web.account.infrastructure.persistence.entity.RoleEntity;
import com.backend.easy_to_web.account.infrastructure.persistence.jpa.AccountJpaRepository;
import com.backend.easy_to_web.account.infrastructure.persistence.jpa.AccountRoleJpaRepository;
import com.backend.easy_to_web.account.infrastructure.persistence.jpa.RoleJpaRepository;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AccountRepositoryAdapter implements AccountRepository {

  private final AccountJpaRepository accountJpaRepository;
  private final AccountRoleJpaRepository accountRoleJpaRepository;
  private final RoleJpaRepository roleJpaRepository;

  @Override
  public Account getByEmail(String email) {
    return findByEmail(email)
        .orElseThrow(()->new CustomIllegalArgumentException(ExceptionMessage.EMAIL_NOT_FOUND));
  }

  @Override
  public Optional<Account> findByEmail(String email) {
    Optional<Account> account = accountJpaRepository.findByEmail(email).map(AccountEntity::toDomain);
    account.ifPresent((ac)-> {
      List<RoleEntity> roles = accountRoleJpaRepository.findAllRoleByAccountId(ac.getId());
      ac.getRoles().addAll(roles.stream().map(RoleEntity::toDomain).toList());
    });

    return account;
  }

  @Override
  public Account save(Account account) {
    AccountEntity saved = accountJpaRepository.save(AccountEntity.of(account));

    // 기존 Role 관계 조회
    Set<String> existRoleIds = accountRoleJpaRepository.findAllByAccountId(account.getId()).stream()
        .map(AccountRoleEntity::getRoleId)
        .collect(Collectors.toSet());

    // 새롭게 저장할 Role 목록
    Set<String> newRoleKeys = account.getRoles().stream()
        .map(Role::getId)
        .collect(Collectors.toSet());

    // 추가해야 할 Role (newRoles - existRoles)
    Set<String> toAdd = new HashSet<>(newRoleKeys);
    toAdd.removeAll(existRoleIds);

    // 삭제해야 할 Role (existRoles - newRoles)
    Set<String> toRemove = new HashSet<>(existRoleIds);
    toRemove.removeAll(newRoleKeys);

    // Role 존재 여부 확인 후 추가
    List<AccountRoleEntity> newRoles = toAdd.stream()
        .filter(roleJpaRepository::existsById) // Role이 존재하는 경우만 추가
        .map(roleId -> new AccountRoleEntity(0L, saved.getId(), roleId))
        .toList();

    if (!newRoles.isEmpty()) {
      accountRoleJpaRepository.saveAll(newRoles);
    }

    // 필요 없는 Role 일괄 삭제
    if (!toRemove.isEmpty()) {
      accountRoleJpaRepository.deleteByAccountIdAndRoleIdNotIn(saved.getId(), toRemove);
    }

    // Role 정보 추가 후 반환
    Account savedDomain = saved.toDomain();
    savedDomain.getRoles().addAll(
        accountRoleJpaRepository.findAllRoleByAccountId(saved.getId()).stream()
            .map(RoleEntity::toDomain)
            .toList()
    );

    return savedDomain;
  }

  @Override
  public List<Account> findAllByIds(List<UUID> ids) {
    return accountJpaRepository.findAllById(ids)
        .stream()
        .map(AccountEntity::toDomain)
        .toList();

  }

  @Override
  public Optional<Account> findById(UUID id) {
    return accountJpaRepository.findById(id).map(AccountEntity::toDomain);
  }

  @Override
  public Account getById(UUID id) {
    return findById(id)
        .orElseThrow(()-> new CustomIllegalArgumentException(ExceptionMessage.ACCOUNT_NOT_FOUND));
  }

}
