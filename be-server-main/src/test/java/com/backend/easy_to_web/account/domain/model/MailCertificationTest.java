package com.backend.easy_to_web.account.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.easy_to_web.test_support.model.UnitTest;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MailCertificationTest extends UnitTest {
  @Test
  @DisplayName("인증 객체 생성 테스트")
  void createCertification_ShouldCreateObjectWithCorrectValues() {
    // given
    String type = "auth";
    String email = "test@example.com";
    String code = "123456";
    int expirationInMinutes = 10;

    // when
    MailCertification certification = MailCertification.createCertification(type, email, code, expirationInMinutes);

    // then
    assertEquals(type, certification.getType());
    assertEquals(email, certification.getEmail());
    assertEquals(code, certification.getCertificationCode());
    assertTrue(certification.getExpirationDate().isAfter(LocalDateTime.now()), "Expiration date should be in the future");
  }

  @Test
  @DisplayName("Key 생성 테스트")
  void createKey_ShouldGenerateCorrectKey() {
    // given
    String type = "auth";
    String email = "test@example.com";

    // when
    String key = MailCertification.createKey(type, email);

    // then
    assertEquals("auth:test@example.com", key);
  }

  @Test
  @DisplayName("남은 시간 계산 테스트")
  void calDurationMillis_ShouldReturnRemainingTimeInMillis() {
    // given
    String type = "auth";
    String email = "test@example.com";
    String code = "123456";
    int expirationInMinutes = 5;

    MailCertification certification = MailCertification.createCertification(type, email, code, expirationInMinutes);

    // when
    long durationMillis = certification.calDurationMillis();

    // then
    assertTrue(durationMillis > 0, "Remaining time should be positive");
    assertTrue(durationMillis <= Duration.ofMinutes(expirationInMinutes).toMillis(),
        "Remaining time should not exceed expiration time");
  }

  @Test
  @DisplayName("코드 검증 테스트")
  void verify_ShouldReturnTrueForCorrectCode() {
    // given
    String type = "auth";
    String email = "test@example.com";
    String code = "123456";
    int expirationInMinutes = 10;

    MailCertification certification = MailCertification.createCertification(type, email, code, expirationInMinutes);

    // when
    boolean isValid = certification.verify(code);

    // then
    assertTrue(isValid, "The code should be valid");
  }

  @Test
  @DisplayName("코드 만료 테스트")
  void expired_ShouldReturnTrueForExpiredCertification() {
    // given
    String type = "auth";
    String email = "test@example.com";
    String code = "123456";
    int expirationInMinutes = -1; // 이미 만료된 시간 설정

    MailCertification certification = MailCertification.createCertification(type, email, code, expirationInMinutes);

    // when
    boolean isExpired = certification.expired();

    // then
    assertTrue(isExpired, "The certification should be expired");
  }
}