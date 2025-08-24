package com.backend.easy_to_web.account.application.handler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.backend.easy_to_web.account.application.service.CertificationMailService;
import com.backend.easy_to_web.account.domain.result.VerifyMailCertificationResult;
import com.backend.easy_to_web.account.domain.usecase.VerifyMailCertificationUseCase;
import com.backend.easy_to_web.test_support.model.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;


@TestInstance(Lifecycle.PER_CLASS)
class VerifyMailCertificationHandlerTest extends UnitTest{

  private static final String TYPE = "testType";
  private static final String EMAIL = "test@example.com";
  private static final String CERTIFICATION_CODE = "123456";

  CertificationMailService certificationMailService = mock(CertificationMailService.class);
  private final VerifyMailCertificationHandler handler = new VerifyMailCertificationHandler(certificationMailService);



  @Test
  @DisplayName("인증번호가 유효하지 않으면 false를 반환한다")
  void shouldReturnFalseWhenInvalidCertificationCode() {
    // given
    String invalidCode = "wrong" + CERTIFICATION_CODE;
    VerifyMailCertificationUseCase command = new VerifyMailCertificationUseCase(TYPE, EMAIL, invalidCode);
    when(certificationMailService.isValidCertification(TYPE, EMAIL, invalidCode)).thenReturn(false);

    // when
    VerifyMailCertificationResult result = handler.handle(command);

    // then
    assertFalse(result.isValid());
  }


  @Test
  @DisplayName("인증번호가 유효하면 true 를 반환한다")
  void shouldReturnTrueWhenValidCertificationCode() {
    // given
    VerifyMailCertificationUseCase command = new VerifyMailCertificationUseCase(TYPE, EMAIL, CERTIFICATION_CODE);
    when(certificationMailService.isValidCertification(TYPE, EMAIL, CERTIFICATION_CODE)).thenReturn(true);

    // when
    VerifyMailCertificationResult result = handler.handle(command);

    // then
    assertTrue(result.isValid());
  }
}