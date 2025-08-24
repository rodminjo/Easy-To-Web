package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.account.domain.usecase.JoinAccountUseCase;
import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AccountJoinRest {

  @Schema(description = "회원가입 요청 데이터")
  public record AccountJoinInput(
      @Schema(description = "메일 인증 타입", example = "VERIFIED_EMAIL")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String type,

      @Schema(description = "이메일", example = "example@gmail.com")
      @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
      @Email(message = ValidationMessage.EMAIL_INVALID)
      String email,

      @Schema(description = "인증 코드", example = "123456")
      @NotBlank(message = ValidationMessage.CERTIFICATION_CODE_REQUIRED)
      String certificationCode,

      @Schema(description = "가입할 비밀번호", example = ValidationMessage.PASSWORD_FORMAT)
      @NotBlank(message = ValidationMessage.PASSWORD_REQUIRED)
      @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN_REGEX, message = ValidationMessage.PASSWORD_PATTERN)
      String password

  ) implements BaseInput<JoinAccountUseCase>{


    @Override
    public JoinAccountUseCase toCommand() {
      return new JoinAccountUseCase(type, email, certificationCode, password);
    }
  }

  @Schema(description = "회원가입 응답 데이터")
  public record AccountJoinOutput(
      @Schema(description = "가입된 이메일", example = "example@gmail.com")
      String email

  ){

  }
}
