package com.backend.easy_to_web.common.domain.exception;

public class ResourceNotFoundException extends BaseException {

  public ResourceNotFoundException(ExceptionMessage exceptionMessage) {
    super(exceptionMessage);
  }
}
