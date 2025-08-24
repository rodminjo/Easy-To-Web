package com.backend.easy_to_web.account.application.handler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.result.SendPasswordChangeMailResult;
import com.backend.easy_to_web.account.domain.usecase.SendPasswordChangeMailUseCase;
import com.backend.easy_to_web.common.application.port.EmailSender;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeAccountRepository;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeEmailSender;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;


@TestInstance(Lifecycle.PER_CLASS)
class PasswordMailSendHandlerTest extends UnitTest {

  private final String email = "test@example.com";
  private final Account account = Account.builder()
      .id(null)
      .email(email)
      .password("password123")
      .nickname("Test User")
      .profileUrl("https://example.com/profile/test")
      .roles(new HashSet<>())
      .build();

  private final FakeAccountRepository accountRepository = new FakeAccountRepository();
  CertificationMailService certificationMailService = mock(CertificationMailService.class);

  @BeforeAll
  void setup() {
    accountRepository.save(account);
  }

  @Test
  @DisplayName("이메일이 존재하지 않을 경우 CustomIllegalArgumentException 을 반환한다")
  void shouldThrowExceptionWhenEmailNotFound() {
    // given
    String newEmail = "new" + email;
    SendPasswordChangeMailUseCase command = new SendPasswordChangeMailUseCase(newEmail);

    EmailSender emailSender = new FakeEmailSender(false);
    SendPasswordChangeMailHandler handler =
        new SendPasswordChangeMailHandler(accountRepository, certificationMailService, emailSender);

    //when & then
    assertThatThrownBy(() -> handler.handle(command))
        .isExactlyInstanceOf(CustomIllegalArgumentException.class)
        .hasMessage(ExceptionMessage.EMAIL_NOT_FOUND.getDescription());
  }


  @Test
  @DisplayName("이메일 전송에 성공하면 문제없이 메서드가 완료된다.")
  void shouldCompleteAuthMailSend() {
    // given
    SendPasswordChangeMailUseCase command = new SendPasswordChangeMailUseCase(email);

    EmailSender emailSender = new FakeEmailSender(false);
    SendPasswordChangeMailHandler handler =
        new SendPasswordChangeMailHandler(accountRepository, certificationMailService, emailSender);

    // when, then
    SendPasswordChangeMailResult result = handler.handle(command);
    assertThat(result.getType()).isEqualTo(SendPasswordChangeMailHandler.EMAIL_PREFIX);
  }
}