package com.backend.easy_to_web.account.domain.result;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.model.Role;
import com.backend.easy_to_web.common.application.result.Result;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Builder
@AllArgsConstructor
public class LoginResult implements Result {
  private UUID id;
  private String email;
  private String password;
  private String nickname;
  private String profileUrl;
  private List<GrantedAuthority> roles;

  public static LoginResult of(Account account){
    List<String> authorities = account.getRoles().stream()
        .map(Role::getId)
        .toList();
    List<GrantedAuthority> roles = createAuthorityList(authorities);

    return LoginResult.builder()
        .id(account.getId())
        .email(account.getEmail())
        .password(account.getPassword())
        .nickname(account.getNickname())
        .profileUrl(account.getProfileUrl())
        .roles(roles)
        .build();
  }
}
