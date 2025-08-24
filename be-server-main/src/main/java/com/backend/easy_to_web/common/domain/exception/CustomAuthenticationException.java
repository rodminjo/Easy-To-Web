package com.backend.easy_to_web.common.domain.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthenticationException extends AuthenticationException {

  private final ExceptionMessage exceptionMessage;

  public CustomAuthenticationException(ExceptionMessage exceptionMessage) {
    super(exceptionMessage.getDescription());
    this.exceptionMessage = exceptionMessage;
  }
}
