package com.backend.easy_to_web.account.domain.port.in;

import com.backend.easy_to_web.account.domain.model.Account;
import java.util.List;
import java.util.UUID;

public interface AccountGateway {

  List<Account> findAllByIds(List<UUID> ids);

  Account getByEmail(String email);

}
