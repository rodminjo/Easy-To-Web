package com.backend.easy_to_web.account.domain.port.out;

import com.backend.easy_to_web.account.domain.model.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

  Account getByEmail(String email);

  Optional<Account> findByEmail(String email);

  Account save(Account account);

  List<Account> findAllByIds(List<UUID> ids);

  Optional<Account> findById(UUID id);

  Account getById(UUID id);

}
