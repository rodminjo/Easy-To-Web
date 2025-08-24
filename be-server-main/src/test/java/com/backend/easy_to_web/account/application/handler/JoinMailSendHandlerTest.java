package com.backend.easy_to_web.account.application.handler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.result.SendJoinMailResult;
import com.backend.easy_to_web.account.domain.usecase.SendJoinMailUseCase;
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
class JoinMailSendHandlerTest extends UnitTest{

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
  void setupAll() {
    accountRepository.save(account);
  }


  @Test
  @DisplayName("이미 존재하는 이메일일 경우 CustomIllegalArgumentException 을 반환한다")
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // given
    SendJoinMailUseCase command = new SendJoinMailUseCase(email);
    EmailSender emailSender = new FakeEmailSender(false);
    JoinMailSendHandler joinMailSendHandler = new JoinMailSendHandler(accountRepository, certificationMailService, emailSender);


    //when & then
    assertThatThrownBy(() -> joinMailSendHandler.handle(command))
        .isExactlyInstanceOf(CustomIllegalArgumentException.class)
        .hasMessage(ExceptionMessage.EMAIL_ALREADY_EXISTS.getDescription());
  }



  @Test
  @DisplayName("이메일 전송에 성공하면 문제없이 메서드가 완료된다.")
  void shouldCompleteAuthMailSend() {
    // given
    String newEmail = "new" + email;
    SendJoinMailUseCase command = new SendJoinMailUseCase(newEmail);
    EmailSender emailSender = new FakeEmailSender(false);
    JoinMailSendHandler joinMailSendHandler = new JoinMailSendHandler(accountRepository, certificationMailService, emailSender);

    // when, then
    SendJoinMailResult result = joinMailSendHandler.handle(command);
    assertThat(result.getType()).isEqualTo(JoinMailSendHandler.EMAIL_PREFIX);
  }

}