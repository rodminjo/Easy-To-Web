package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.project.domain.usecase.AcceptProjectInvitationUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class ProjectInviteAcceptRest {
  @Schema(description = "프로젝트 초대 승인 요청 데이터")
  public record ProjectInviteAcceptInput(
      @Schema(description = "초대 인증코드", example = "qwertqwertqwertqwert")
      @NotBlank(message = ValidationMessage.CERTIFICATION_CODE_REQUIRED)
      String code
  ){

    public AcceptProjectInvitationUseCase toCommand(UUID projectId) {
      return new AcceptProjectInvitationUseCase(projectId, code);
    }
  }

  @Schema(description = "프로젝트 초대 승인 응답 데이터")
  public record ProjectInviteAcceptOutput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      UUID id
  ){
  }


}
