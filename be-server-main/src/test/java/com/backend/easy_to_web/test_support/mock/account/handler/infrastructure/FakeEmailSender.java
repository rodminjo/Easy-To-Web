package com.backend.easy_to_web.test_support.mock.account.handler.infrastructure;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.port.EmailSender;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeEmailSender implements EmailSender {

  private final boolean isException;

  @Override
  public void send(String to, Map<String, String> variables) {
    if (isException) {
      throw new CustomIllegalStateException(ExceptionMessage.MAIL_WRITE_FAILED);
    }
  }
}
