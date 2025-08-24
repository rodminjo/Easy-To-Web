package com.backend.easy_to_web.security.authentication.token;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class JwtRefreshToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = 620L;

  // 아이디
  private final Object principal;

  // 비밀번호
  private Object credentials;


  public JwtRefreshToken(Object principal, Object credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    this.setAuthenticated(false);
  }

  public JwtRefreshToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true);
  }

  public Object getCredentials() {
    return this.credentials;
  }

  public Object getPrincipal() {
    return this.principal;
  }

  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
    super.setAuthenticated(false);
  }

  public void eraseCredentials() {
    super.eraseCredentials();
    this.credentials = null;
  }
}
