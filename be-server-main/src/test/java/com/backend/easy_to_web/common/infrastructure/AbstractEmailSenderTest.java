package com.backend.easy_to_web.common.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backend.easy_to_web.common.application.port.EmailSender;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.external.AbstractEmailSender;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;


@ExtendWith({MockitoExtension.class})
@TestInstance(Lifecycle.PER_CLASS)
class AbstractEmailSenderTest extends UnitTest{

  @Mock
  private JavaMailSender javaMailSender;
  @Mock
  private MimeMessage mineMessage;

  String email = "test@example.com";
  String subject = "subject";
  String templatePath = "email/auth-mail.html";

  EmailSender emailSender;

  @BeforeEach
  void setupEach() {
    emailSender = new AbstractEmailSender(javaMailSender) {
      @Override
      protected String getSubject() {
        return subject;
      }

      @Override
      protected String getTemplatePath() {
        return templatePath;
      }
    };
  }

  @Test
  @DisplayName("메일 작성중 오류가 발생하면 예외를 반환한다")
  void shouldThrowExceptionWhenCreateEmailFailed() throws Exception {
    // given
    when(javaMailSender.createMimeMessage()).thenReturn(mineMessage);
    doThrow(new MessagingException()).when(mineMessage).setSubject(anyString());

    // when, then
    assertThatThrownBy(() -> emailSender.send(email, Map.of()))
        .isExactlyInstanceOf(CustomIllegalStateException.class)
        .hasMessage(ExceptionMessage.MAIL_WRITE_FAILED.getDescription());
  }

  @Test
  @DisplayName("메일 템플릿을 불러오는데 발생하면 예외를 반환한다")
  void shouldThrowExceptionWhenEmailTemplateNotFound() throws Exception {
    // given
    given(javaMailSender.createMimeMessage()).willAnswer(
        invocation -> {throw new IOException("test");}
    );

    // when, then
    assertThatThrownBy(() -> emailSender.send(email, Map.of()))
        .isExactlyInstanceOf(CustomIllegalStateException.class)
        .hasMessage(ExceptionMessage.MAIL_FILE_ERROR.getDescription());
  }

  @Test
  @DisplayName("메일 발송에 실패하면 예외를 반환한다")
  void shouldThrowExceptionWhenEmailSendFailed() throws Exception {
    // given
    when(javaMailSender.createMimeMessage()).thenReturn(mineMessage);
    doThrow(new MailSendException("test")).when(mineMessage).setSubject(anyString());

    // when, then
    assertThatThrownBy(() -> emailSender.send(email, Map.of()))
        .isExactlyInstanceOf(CustomIllegalStateException.class)
        .hasMessage(ExceptionMessage.MAIL_SEND_FAILED.getDescription());
  }

  @Test
  @DisplayName("메일 발송에 성공하면 발송한 인증코드를 포함한 Result를 반환한다")
  void shouldCallJavaMailSenderSendWhenEmailSendSuccess() throws Exception {
    // given
    MimeMessage mimeMessage = mock(MimeMessage.class); // MimeMessage 객체 Mocking
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    // when
    emailSender.send(email, Map.of());

    // then
    verify(javaMailSender).send(mimeMessage); // javaMailSender의 send() 호출 여부 검증
  }
}