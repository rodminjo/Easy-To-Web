package com.backend.easy_to_web.account.domain.usecase;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.model.AccountSNS;
import com.backend.easy_to_web.account.domain.model.SNSProvider;
import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.utils.RandomUtils;
import java.util.HashSet;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class LoginOrRegisterAccountUseCase implements UseCase {

  private final String nameAttribute; // 고유 인증
  private final String name;
  private final String email;
  private final String picture;
  private final SNSProvider provider;

  @Builder
  public LoginOrRegisterAccountUseCase(String nameAttribute, String name, String email, String picture, SNSProvider provider) {
    this.nameAttribute = nameAttribute;
    this.name = name;
    this.email = email;
    this.picture = picture;
    this.provider = provider;
  }

  public Account createAccount(PasswordEncoder passwordEncoder) {
    return Account.builder()
        .id(null)
        .email(email)
        .password(passwordEncoder.encode(RandomUtils.generateRandomMixAll(10)))
        .nickname(name)
        .profileUrl(picture)
        .roles(new HashSet<>())
        .build();
  }

  public AccountSNS createAccountSNS(Account account) {
    return AccountSNS.builder()
        .id(null)
        .accountId(account.getId())
        .email(email)
        .providerAccountId(nameAttribute)
        .provider(provider)
        .build();
  }
}
