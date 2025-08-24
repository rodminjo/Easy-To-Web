package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.InviteProjectUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectInviteRest {
  @Schema(description = "프로젝트 초대 요청 데이터")
  public record ProjectInviteInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId,

      @Schema(description = "초대 계정 이메일", example = "example@naver.com")
      @NotNull(message = ValidationMessage.NOT_NULL)
      String email
  ) implements BaseInput<InviteProjectUseCase> {

    @Override
    public InviteProjectUseCase toCommand() {
      return new InviteProjectUseCase(projectId, email);
    }
  }


}
