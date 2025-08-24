package com.backend.easy_to_web.account.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
public class Account extends Domain {

  private UUID id;
  private String email;
  private String password;
  private String nickname;
  private String profileUrl;

  private Set<Role> roles;

  @Builder
  public Account(LocalDateTime createdDate, LocalDateTime modifiedDate, String createdBy,
      String updatedBy, UUID id, String email, String password, String nickname, String profileUrl,
      Set<Role> roles) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.profileUrl = profileUrl;
    this.roles = roles;
  }

  public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
    this.password = passwordEncoder.encode(newPassword);
  }

  public void update(String nickname, String profileUrl){
    this.nickname = nickname;
    this.profileUrl = profileUrl;
  }
}
