package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.account.domain.usecase.SendPasswordChangeMailUseCase;
import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordMailSendRest {

  @Schema(description = "비밀번호 변경 이메일 전송 input")
  public record PasswordMailSendInput(
      @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
      @Email(message = ValidationMessage.EMAIL_INVALID)
      @Schema(description = "이메일", example = "example@gmail.com")
      String email
  ) implements BaseInput<SendPasswordChangeMailUseCase> {


    @Override
    public SendPasswordChangeMailUseCase toCommand() {
      return new SendPasswordChangeMailUseCase(email);
    }
  }


  @Schema(description = "비밀번호 변경 이메일 전송 output")
  public record PasswordMailSendOutput(
      @Schema(description = "이메일 인증 타입", example = "PASSWORD_CHANGE_EMAIL")
      String type
  ) {
  }
}
