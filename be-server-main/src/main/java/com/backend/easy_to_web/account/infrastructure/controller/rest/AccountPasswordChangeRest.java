package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.account.domain.usecase.PasswordChangeUseCase;
import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AccountPasswordChangeRest {

  @Schema(description = "회원가입 요청 데이터")
  public record AccountPasswordChangeInput(
      @Schema(description = "메일 인증 타입", example = "PASSWORD_CHANGE_EMAIL")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String type,

      @Schema(description = "이메일", example = "example@gmail.com")
      @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
      @Email(message = ValidationMessage.EMAIL_INVALID)
      String email,

      @Schema(description = "인증 코드", example = "123456")
      @NotBlank(message = ValidationMessage.CERTIFICATION_CODE_REQUIRED)
      String certificationCode,

      @Schema(description = "신규 비밀번호", example = ValidationMessage.PASSWORD_FORMAT)
      @NotBlank(message = ValidationMessage.PASSWORD_REQUIRED)
      @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN_REGEX, message = ValidationMessage.PASSWORD_PATTERN)
      String password

  ) implements BaseInput<PasswordChangeUseCase>{


    @Override
    public PasswordChangeUseCase toCommand() {
      return new PasswordChangeUseCase(type, email, certificationCode, password);
    }
  }

}
