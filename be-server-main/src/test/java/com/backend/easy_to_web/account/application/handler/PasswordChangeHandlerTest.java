package com.backend.easy_to_web.account.application.handler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.usecase.PasswordChangeUseCase;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeAccountRepository;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


class PasswordChangeHandlerTest extends UnitTest{

  private static final String TYPE = "testType";
  private static final String INVALID_EMAIL = "test_invalid@example.com";
  private static final String NOT_EXIST_EMAIL = "test_not_exist@example.com";
  private static final String VALID_EMAIL = "test@example.com";
  private static final String CERTIFICATION_CODE = "123456";
  private static final String PASSWORD = "testPassword12!";

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final FakeAccountRepository accountRepository = new FakeAccountRepository();
  private final CertificationMailService certificationMailService = mock(CertificationMailService.class);
  PasswordChangeHandler handler = new PasswordChangeHandler(certificationMailService, accountRepository, passwordEncoder);

  @Test
  @DisplayName("메일 인증에 실패할 경우 Exception을 반환한다")
  void shouldThrowExceptionWhenEmailCertificationFailed() {
    // given
    PasswordChangeUseCase command = new PasswordChangeUseCase(TYPE, INVALID_EMAIL, CERTIFICATION_CODE, PASSWORD);
    when(certificationMailService.isValidCertification(TYPE, INVALID_EMAIL, CERTIFICATION_CODE))
        .thenReturn(false);

    // when & then
    assertThatThrownBy(() -> handler.handleVoid(command))
        .isExactlyInstanceOf(CustomIllegalArgumentException.class)
        .hasMessage(ExceptionMessage.CERTIFICATION_FAILED.getDescription());
  }

  @Test
  @DisplayName("이메일 계정이 없을 경우 Exception을 반환한다")
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // given
    PasswordChangeUseCase command = new PasswordChangeUseCase(TYPE, NOT_EXIST_EMAIL, CERTIFICATION_CODE, PASSWORD);
    when(certificationMailService.isValidCertification(TYPE, NOT_EXIST_EMAIL, CERTIFICATION_CODE))
        .thenReturn(true);

    // when & then
    assertThatThrownBy(() -> handler.handleVoid(command))
        .isExactlyInstanceOf(CustomIllegalArgumentException.class)
        .hasMessage(ExceptionMessage.EMAIL_NOT_FOUND.getDescription());
  }

  @Test
  @DisplayName("정상적인 경우 문제없이 비밀번호 변경이 완료된다")
  void shouldReturnWhenPasswordChangeSucceeds() {
    // given
    String newPassword = "new" + PASSWORD;
    Account givenAccount = Account.builder()
        .id(null)
        .email(VALID_EMAIL)
        .password(passwordEncoder.encode(PASSWORD))
        .nickname(VALID_EMAIL)
        .profileUrl("")
        .roles(new HashSet<>())
        .build();
    accountRepository.save(givenAccount);
    PasswordChangeUseCase command = new PasswordChangeUseCase(TYPE, VALID_EMAIL, CERTIFICATION_CODE, newPassword);
    when(certificationMailService.isValidCertification(TYPE, VALID_EMAIL, CERTIFICATION_CODE))
        .thenReturn(true);

    // when
    handler.handleVoid(command);

    // then
    Optional<Account> account = accountRepository.findByEmail(VALID_EMAIL);
    assertTrue(account.isPresent());
    assertTrue(passwordEncoder.matches(newPassword, account.get().getPassword()));
  }

}