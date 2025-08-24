package com.backend.easy_to_web.account.application.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.account.domain.usecase.LoginUseCase;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeAccountRepository;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;


@TestInstance(Lifecycle.PER_CLASS)
class LoginHandlerTest extends UnitTest {

  String email = "test@example.com";
  Account account = Account.builder()
      .id(null)
      .email(email)
      .password("password123")
      .nickname("Test User")
      .profileUrl("https://example.com/profile/test")
      .roles(new HashSet<>())
      .build();
  private final FakeAccountRepository accountRepository = new FakeAccountRepository();
  LoginHandler loginHandler = new LoginHandler(accountRepository);

  @BeforeAll
  void setup() {
    accountRepository.save(account);
  }

  @Test
  @DisplayName("올바른 이메일로 로그인 성공 시 AccountLoginResult를 반환한다")
  void shouldReturnAccountLoginResultWhenLoginSucceeds() {
    // given
    LoginUseCase command = new LoginUseCase(email);

    // when
    LoginResult result = loginHandler.handle(command);

    // then
    assertNotNull(result);
    assertEquals(account.getEmail(), result.getEmail());
    assertEquals(account.getNickname(), result.getNickname());
    assertEquals(account.getProfileUrl(), result.getProfileUrl());
  }

  @Test
  @DisplayName("없는 이메일로 로그인 시 CustomAuthenticationException을 던진다")
  void shouldThrowExceptionWhenEmailNotFound() {
    // given
    String wrongEmail = "wrong@example.com";
    LoginUseCase command = new LoginUseCase(wrongEmail);

    // then & when
    assertThrows(CustomAuthenticationException.class, () -> loginHandler.handle(command));
  }

}