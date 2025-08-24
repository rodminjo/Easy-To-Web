package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.account.domain.usecase.UpdateProfileUseCase;
import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRest {

  @Schema(description = "계정정보 수정 output")
  public record UpdateProfileInput(
      @Schema(description = "계정 닉네임", example = "닉네임")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String nickname,
      @Schema(description = "계정 프로필 이미지", example = "프로필 이미지 URL")
      String profileUrl
  ) implements BaseInput<UpdateProfileUseCase> {

    @Override
    public UpdateProfileUseCase toCommand() {
      return new UpdateProfileUseCase(nickname, profileUrl);
    }
  }
}
