package com.backend.easy_to_web.common.domain.exception;

public class CustomIllegalArgumentException extends BaseException {

  public CustomIllegalArgumentException(ExceptionMessage exceptionMessage) {
    super(exceptionMessage);
  }

  public CustomIllegalArgumentException(ExceptionMessage exceptionMessage, String additionalMessage) {
    super(exceptionMessage, additionalMessage);
  }
}
