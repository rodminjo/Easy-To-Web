package com.backend.easy_to_web.test_support.mock;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.security.authentication.service.AccountContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class FakeUserDetailService implements UserDetailsService {

  private final LoginResult account;

  public FakeUserDetailService(LoginResult account) {
    this.account = account;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return new AccountContext(account, account.getRoles());
  }
}
