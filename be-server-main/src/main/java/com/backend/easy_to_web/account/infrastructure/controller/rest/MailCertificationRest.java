package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.account.domain.usecase.VerifyMailCertificationUseCase;
import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class MailCertificationRest {

  @Schema(description = "이메일 인증번호 검증 input")
  public record MailCertificationInput(
      @Schema(description = "메일 인증 타입", example = "VERIFIED_EMAIL")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String type,

      @Schema(description = "이메일", example = "example@gmail.com")
      @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
      @Email(message = ValidationMessage.EMAIL_INVALID)
      String email,

      @Schema(description = "인증 코드", example = "123456")
      @NotBlank(message = ValidationMessage.CERTIFICATION_CODE_REQUIRED)
      String certificationCode

  ) implements BaseInput<VerifyMailCertificationUseCase> {

    public VerifyMailCertificationUseCase toCommand(){
      return new VerifyMailCertificationUseCase(type, email, certificationCode);
    }
  }



  @Schema(description = "이메일 인증번호 검증 응답 데이터")
  public record MailCertificationOutput(
      @Schema(description = "검증 결과", example = "true")
      boolean isValid
  ) {

  }
}
