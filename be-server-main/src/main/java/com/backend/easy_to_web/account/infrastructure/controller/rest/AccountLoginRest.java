package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AccountLoginRest {

  @Schema(description = "로그인 요청 데이터")
  public record AccountLoginInput(
      @Schema(description = "이메일", example = "example@gmail.com")
      @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
      @Email(message = ValidationMessage.EMAIL_INVALID)
      String email,

      @Schema(description = "비밀번호", example = ValidationMessage.PASSWORD_FORMAT)
      @NotBlank(message = ValidationMessage.PASSWORD_REQUIRED)
      @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN_REGEX, message = ValidationMessage.PASSWORD_PATTERN)
      String password
  ) {

  }
}
