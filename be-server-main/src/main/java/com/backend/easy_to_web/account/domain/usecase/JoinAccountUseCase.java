package com.backend.easy_to_web.account.domain.usecase;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.common.application.usecase.UseCase;
import java.util.HashSet;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class JoinAccountUseCase implements UseCase {

  private final String type;
  private final String email;
  private final String certificationCode;
  private final String password;
  private final String nickname;

  public JoinAccountUseCase(String type, String email, String certificationCode, String password) {
    this.type = type;
    this.email = email;
    this.certificationCode = certificationCode;
    this.password = password;
    this.nickname = email;
  }

  public Account createAccount(PasswordEncoder passwordEncoder) {
    return Account.builder()
        .id(null)
        .email(email)
        .password(passwordEncoder.encode(password))
        .nickname(nickname)
        .profileUrl("")
        .roles(new HashSet<>())
        .build();
  }
}
