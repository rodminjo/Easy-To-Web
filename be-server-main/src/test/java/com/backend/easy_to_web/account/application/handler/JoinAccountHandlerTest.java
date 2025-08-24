package com.backend.easy_to_web.account.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.domain.result.JoinAccountResult;
import com.backend.easy_to_web.account.domain.usecase.JoinAccountUseCase;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeAccountRepository;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


class JoinAccountHandlerTest extends UnitTest {

  private static final String TYPE = "testType";
  private static final String INVALID_EMAIL = "test_invalid@example.com";
  private static final String EXIST_EMAIL = "test_exist@example.com";
  private static final String VALID_EMAIL = "test@example.com";
  private static final String CERTIFICATION_CODE = "123456";
  private static final String PASSWORD = "testPassword12!";

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final AccountRepository accountRepository = new FakeAccountRepository();

  private CertificationMailService certificationMailService;
  private JoinAccountHandler handler;

  @BeforeEach
  void setup() {
    certificationMailService = mock(CertificationMailService.class);
    handler = new JoinAccountHandler(certificationMailService, accountRepository, passwordEncoder);
  }

  @Test
  @DisplayName("메일 인증에 실패할 경우 Exception을 반환한다")
  void shouldThrowExceptionWhenEmailCertificationFailed() {
    // given
    JoinAccountUseCase command = new JoinAccountUseCase(TYPE, INVALID_EMAIL, CERTIFICATION_CODE, PASSWORD);
    when(certificationMailService.isValidCertification(TYPE, INVALID_EMAIL, CERTIFICATION_CODE))
        .thenReturn(false);

    // when & then
    assertThatThrownBy(() -> handler.handle(command))
        .isExactlyInstanceOf(CustomIllegalArgumentException.class)
        .hasMessage(ExceptionMessage.CERTIFICATION_FAILED.getDescription());
  }

  @Test
  @DisplayName("중복된 이메일이 있을 경우 Exception을 반환한다")
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // given
    when(certificationMailService.isValidCertification(TYPE, EXIST_EMAIL, CERTIFICATION_CODE))
        .thenReturn(true);

    Account account = Account.builder()
        .email(EXIST_EMAIL)
        .password(passwordEncoder.encode(PASSWORD))
        .nickname(EXIST_EMAIL)
        .profileUrl("")
        .roles(new HashSet<>())
        .build();

    accountRepository.save(account);

    JoinAccountUseCase command = new JoinAccountUseCase(TYPE, EXIST_EMAIL, CERTIFICATION_CODE, PASSWORD);

    // when & then
    assertThatThrownBy(() -> handler.handle(command))
        .isExactlyInstanceOf(CustomIllegalArgumentException.class)
        .hasMessage(ExceptionMessage.EMAIL_ALREADY_EXISTS.getDescription());
  }

  @Test
  @DisplayName("정상적으로 가입했을 경우 result를 반환한다")
  void shouldReturnAccountJoinResultWhenJoinSucceeds() {
    // given
    when(certificationMailService.isValidCertification(TYPE, VALID_EMAIL, CERTIFICATION_CODE))
        .thenReturn(true);

    JoinAccountUseCase command = new JoinAccountUseCase(TYPE, VALID_EMAIL, CERTIFICATION_CODE, PASSWORD);

    // when
    JoinAccountResult result = handler.handle(command);

    // then
    assertThat(result.getEmail()).isEqualTo(VALID_EMAIL);
    assertTrue(accountRepository.findByEmail(VALID_EMAIL).isPresent());
  }
}