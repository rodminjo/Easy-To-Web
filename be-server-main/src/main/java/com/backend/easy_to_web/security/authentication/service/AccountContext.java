package com.backend.easy_to_web.security.authentication.service;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 스프링에서 UserDetails를 구현하기 쉽도록 User이라는 클래스를 만들어놓음
 * 해당 클래스를 상속받아 구현
 * */
@Getter
public class AccountContext extends User {

  private final LoginResult account;

  // 생성자를 작성하고 User클래스로 id, pw, 권한을 넘겨준다
  public AccountContext(LoginResult account, Collection<? extends GrantedAuthority> authorities) {
    super(account.getEmail(), account.getPassword(), authorities);
    this.account = account;
  }
}

