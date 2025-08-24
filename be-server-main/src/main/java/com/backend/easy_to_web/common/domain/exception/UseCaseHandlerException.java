package com.backend.easy_to_web.common.domain.exception;

public class UseCaseHandlerException extends BaseException {

  public UseCaseHandlerException(ExceptionMessage exceptionMessage) {
    super(exceptionMessage);
  }
}
