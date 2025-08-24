package com.backend.easy_to_web.security.authentication.token;

import java.io.Serial;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

@Getter
public class JwtLoginToken extends AbstractAuthenticationToken {

  @Serial
  private static final long serialVersionUID = 620L;

  // 아이디
  private final Object principal;

  // 비밀번호
  private final Object credentials;


  public JwtLoginToken(Object principal, Object credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    this.setAuthenticated(false);
  }

  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
    super.setAuthenticated(false);
  }
}
