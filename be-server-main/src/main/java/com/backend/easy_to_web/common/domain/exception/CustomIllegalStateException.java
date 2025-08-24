package com.backend.easy_to_web.common.domain.exception;

public class CustomIllegalStateException extends BaseException{

  public CustomIllegalStateException(ExceptionMessage exceptionMessage) {
    super(exceptionMessage);
  }

  public CustomIllegalStateException(ExceptionMessage exceptionMessage, String additionalMessage) {
    super(exceptionMessage, additionalMessage);
  }
}
