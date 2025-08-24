package com.backend.easy_to_web.test_support.mock.account.handler.infrastructure;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FakeAccountRepository implements AccountRepository {

  private final List<Account> data = Collections.synchronizedList(new ArrayList<>());

  public FakeAccountRepository() {
    createTestAccounts();
  }

  private void createTestAccounts() {
    for (int i = 1; i <= 10; i++) {
      UUID id = UUID.randomUUID();
      String email = "user" + i + "@example.com";
      String password = "password" + i;
      String name = "User " + i;
      String profileUrl = "https://example.com/profile/user" + i;

      Account account = Account.builder()
          .id(id)
          .email(email)
          .password(password)
          .nickname(name)
          .profileUrl(profileUrl)
          .roles(Collections.emptySet())
          .build();
      data.add(account);
    }
  }


  @Override
  public Account getByEmail(String email) {
    return findByEmail(email).get();
  }

  @Override
  public Optional<Account> findByEmail(String email) {
    return data.stream()
        .filter(account -> account.getEmail().equals(email))
        .findFirst();
  }

  @Override
  public Account save(Account joinAccount) {
    if (joinAccount.getId() == null || findByEmail(joinAccount.getEmail()).isEmpty()) {
      Account newUser = Account.builder()
          .id(UUID.randomUUID())
          .email(joinAccount.getEmail())
          .password(joinAccount.getPassword())
          .nickname(joinAccount.getNickname())
          .profileUrl(joinAccount.getProfileUrl())
          .roles(new HashSet<>())
          .build();
      newUser.getRoles().addAll(joinAccount.getRoles());

      data.add(newUser);
      return newUser;

    } else {
      data.removeIf(item -> Objects.equals(item.getId(), joinAccount.getId()));
      data.add(joinAccount);
      return joinAccount;
    }
  }

  @Override
  public List<Account> findAllByIds(List<UUID> ids) {
    HashSet<UUID> set = new HashSet<>(ids);
    return data.stream()
        .filter(item -> set.contains(item.getId()))
        .toList();
  }

  @Override
  public Optional<Account> findById(UUID id) {
    return data.stream()
        .filter(account -> account.getId().equals(id))
        .findFirst();

  }

  @Override
  public Account getById(UUID id) {
    return findById(id).get();
  }
}
