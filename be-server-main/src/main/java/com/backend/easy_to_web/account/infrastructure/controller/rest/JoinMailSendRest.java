package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.account.domain.usecase.SendJoinMailUseCase;
import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class JoinMailSendRest {

  @Schema(description = "인증 이메일 전송 input")
  public record JoinMailSendInput(
      @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
      @Email(message = ValidationMessage.EMAIL_INVALID)
      @Schema(description = "이메일", example = "example@gmail.com")
      String email
  ) implements BaseInput<SendJoinMailUseCase> {


    @Override
    public SendJoinMailUseCase toCommand() {
      return new SendJoinMailUseCase(email);
    }
  }


  @Schema(description = "인증 이메일 전송 output")
  public record JoinMailSendOutput(
      @Schema(description = "이메일 인증 타입", example = "VERIFIED_EMAIL")
      String type
  ) {
  }
}
