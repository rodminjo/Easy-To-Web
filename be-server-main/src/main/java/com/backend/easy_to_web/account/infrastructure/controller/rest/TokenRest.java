package com.backend.easy_to_web.account.infrastructure.controller.rest;


import com.backend.easy_to_web.security.authentication.token.AccountToken.AccountRecord;
import lombok.Builder;

public class TokenRest {

  @Builder
  public record TokenOutput (
      String tokenType,
      String accessToken,
      String refreshToken,
      AccountRecord account
  ) {

  }
}
