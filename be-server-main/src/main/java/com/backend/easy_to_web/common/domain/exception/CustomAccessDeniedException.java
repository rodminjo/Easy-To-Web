package com.backend.easy_to_web.common.domain.exception;

import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class CustomAccessDeniedException extends AccessDeniedException {

  private final ExceptionMessage exceptionMessage;

  public CustomAccessDeniedException(ExceptionMessage exceptionMessage) {
    super(exceptionMessage.getDescription());
    this.exceptionMessage = exceptionMessage;
  }
}
