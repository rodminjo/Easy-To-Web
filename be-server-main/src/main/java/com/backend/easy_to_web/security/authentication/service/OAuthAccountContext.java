package com.backend.easy_to_web.security.authentication.service;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


@Getter
public class OAuthAccountContext extends DefaultOAuth2User {

  private final LoginResult account;

  public OAuthAccountContext(
      LoginResult account,
      Collection<? extends GrantedAuthority> authorities,
      Map<String, Object> attributes,
      String nameAttributeKey
  ) {
    super(authorities, attributes, nameAttributeKey);
    this.account = account;
  }
}

