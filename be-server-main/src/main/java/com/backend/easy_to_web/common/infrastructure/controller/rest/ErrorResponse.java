package com.backend.easy_to_web.common.infrastructure.controller.rest;

import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {

  private String errorCode;
  private String errorDescription;
  private String timeStamp;

  public ErrorResponse(ExceptionMessage exceptionMessage) {
    this.errorCode = exceptionMessage.getErrorCode();
    this.errorDescription = exceptionMessage.getDescription();
    this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }


  public ErrorResponse(String errorCode, String errorDescription) {
    this.errorCode = errorCode;
    this.errorDescription = errorDescription;
    this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}