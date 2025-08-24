package com.backend.easy_to_web.common.domain.exception;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class BaseException extends RuntimeException {

  private final ExceptionMessage exceptionMessage;

  public BaseException(ExceptionMessage exceptionMessage) {
    this(exceptionMessage, "");
  }

  public BaseException(ExceptionMessage exceptionMessage, String additionalMessage) {
    super(mergeAdditionalMessage(exceptionMessage.getDescription(), additionalMessage));
    this.exceptionMessage = exceptionMessage;
  }

  private static String mergeAdditionalMessage(String exceptionMessage, String additionalMessage){
    if (StringUtils.hasText(additionalMessage)){
      return String.format("%s (%s)", exceptionMessage, additionalMessage);
    }

    return exceptionMessage;
  }

}
