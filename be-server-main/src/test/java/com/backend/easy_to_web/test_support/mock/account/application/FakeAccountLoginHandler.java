package com.backend.easy_to_web.test_support.mock.account.application;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.account.domain.usecase.LoginUseCase;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.util.List;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;


public class FakeAccountLoginHandler implements UseCaseHandler<LoginResult, LoginUseCase> {

  private final PasswordEncoder passwordEncoder;

  public FakeAccountLoginHandler(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Class<LoginUseCase> getUseCaseClass() {
    return LoginUseCase.class;
  }

  @Override
  public LoginResult handle(LoginUseCase useCase) {
    if (!useCase.getEmail().equals("test@example.com")){
      throw new CustomAuthenticationException(ExceptionMessage.LOGIN_INFO_INVALID);
    }

    return LoginResult.builder()
        .email("test@example.com")
        .password(passwordEncoder.encode("password"))
        .nickname("testUser")
        .profileUrl("https://example.com/profile.jpg")
        .roles(AuthorityUtils.createAuthorityList(List.of("ROLE_USER")))
        .build();
  }
}
