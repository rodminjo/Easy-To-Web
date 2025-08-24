package com.backend.easy_to_web.account.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.backend.easy_to_web.account.domain.model.MailCertification;
import com.backend.easy_to_web.common.application.port.CodeGenerator;
import com.backend.easy_to_web.common.application.port.EmailSender;
import com.backend.easy_to_web.common.application.port.RedisRepository;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.MailProperties;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeEmailSender;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeRedisRepository;
import com.backend.easy_to_web.test_support.mock.properties.PropertiesContainer;
import com.backend.easy_to_web.test_support.mock.utils.MockCodeGenerator;
import com.backend.easy_to_web.test_support.model.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CertificationMailServiceTest extends UnitTest {

  String prefix = "test_prefix";
  String email = "test_email@example.com";
  String code = "123456";

  private final MailCertification certification = MailCertification.createCertification(
      prefix,
      email,
      code,
      1
  );

  private final PropertiesContainer propertiesContainer = new PropertiesContainer();
  private final CodeGenerator codeGenerator = new MockCodeGenerator(code);
  private final MailProperties mailProperties = propertiesContainer.getMailProperties();


  @Test
  @DisplayName("이메일 발송이 실패하면 에러를 반환한다.")
   void shouldThrowExceptionWhenEmailDontSend() {
    // given
    RedisRepository redisRepository = new FakeRedisRepository(false);
    EmailSender emailSender = new FakeEmailSender(true);
    CertificationMailService service = new CertificationMailService(redisRepository, codeGenerator, mailProperties);

    // when, then
    assertThatThrownBy(() -> service.sendAndSaveCertificationCode(prefix, email, emailSender))
        .isExactlyInstanceOf(CustomIllegalStateException.class);
  }


  @Test
  @DisplayName("redis에 저장이 실패하면 에러를 반환한다.")
  void shouldThrowExceptionWhenNotSavedInRedis() {
    // given
    RedisRepository redisRepository = new FakeRedisRepository(true);
    EmailSender emailSender = new FakeEmailSender(false);
    CertificationMailService service = new CertificationMailService(redisRepository, codeGenerator, mailProperties);

    // when, then
    assertThatThrownBy(() -> service.sendAndSaveCertificationCode(prefix, email, emailSender))
        .isExactlyInstanceOf(CustomIllegalStateException.class)
        .hasMessage(ExceptionMessage.REDIS_FAILED.getDescription());
  }

  @Test
  @DisplayName("보낸 이력이 없다면 false를 반환한다.")
  void returnFalseWhenNotFoundCertification() {
    // given
    RedisRepository redisRepository = new FakeRedisRepository(false);
    CertificationMailService service = new CertificationMailService(redisRepository, codeGenerator, mailProperties);

    // when
    boolean result = service.isValidCertification(prefix, email, code);

    // then
    assertFalse(result);
  }

  @Test
  @DisplayName("인증번호가 만료되면 false 를 반환한다")
  void shouldReturnFalseWhenExpiredCertificationCode(){
    // given
    String expiredType = prefix + "expired";
    MailCertification expiredCertification = MailCertification.createCertification(
        expiredType,
        email,
        code,
        0
    );
    RedisRepository redisRepository = new FakeRedisRepository(false);
    redisRepository.setWithExpired(expiredCertification.createKey(), expiredCertification, expiredCertification.calDurationMillis());
    CertificationMailService service = new CertificationMailService(redisRepository, codeGenerator, mailProperties);

    // when
    boolean result = service.isValidCertification(expiredType, email, code);

    // then
    assertFalse(result);
  }

  @Test
  @DisplayName("이메일 혹은 타입이 유효하지 않으면 false를 반환한다")
  void shouldReturnFalseWhenInvalidEmailAndType() {
    // given
    String invalidPrefix = "invalidType";
    String invalidEmail = "invalid@example.com";
    MailCertification certification1 = MailCertification.createCertification(invalidPrefix, email, code, 1);
    MailCertification certification2 = MailCertification.createCertification(prefix, invalidEmail, code, 1);
    MailCertification certification3 = MailCertification.createCertification(invalidPrefix, invalidEmail, code, 1);

    RedisRepository redisRepository = new FakeRedisRepository(false);
    redisRepository.setWithExpired(certification.createKey(), certification, certification.calDurationMillis());
    CertificationMailService service = new CertificationMailService(redisRepository, codeGenerator, mailProperties);

    // when
    boolean result1 = service.isValidCertification(certification1.getType(), certification1.getEmail(), certification1.getCertificationCode());
    boolean result2 = service.isValidCertification(certification2.getType(), certification2.getEmail(), certification2.getCertificationCode());
    boolean result3 = service.isValidCertification(certification3.getType(), certification3.getEmail(), certification3.getCertificationCode());

    // then
    assertFalse(result1);
    assertFalse(result2);
    assertFalse(result3);
  }

  @Test
  @DisplayName("인증 코드가 유효하지 않으면 false를 반환한다.")
  void returnFalseWhenInvalidCertification() {
    // given
    String invalidCode = "invalid" + code;

    RedisRepository redisRepository = new FakeRedisRepository(false);
    redisRepository.setWithExpired(certification.createKey(), certification, certification.calDurationMillis());
    CertificationMailService service = new CertificationMailService(redisRepository, codeGenerator, mailProperties);

    // when
    boolean result = service.isValidCertification(prefix, email, invalidCode);

    // then
    assertFalse(result);
  }

  @Test
  @DisplayName("유효하면 true를 반환한다.")
  void returnTrueWhenValidCertification() {
    // given
    RedisRepository redisRepository = new FakeRedisRepository(false);
    redisRepository.setWithExpired(certification.createKey(), certification, certification.calDurationMillis());
    CertificationMailService service = new CertificationMailService(redisRepository, codeGenerator, mailProperties);

    // when, then
    // when
    boolean result = service.isValidCertification(prefix, email, code);

    // then
    Assertions.assertTrue(result);
  }

}