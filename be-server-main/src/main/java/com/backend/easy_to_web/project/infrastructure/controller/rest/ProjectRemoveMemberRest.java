package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.RemoveProjectMemberUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectRemoveMemberRest {
  @Schema(description = "프로젝트 멤버 방출 요청 데이터")
  public record ProjectRemoveMemberInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId,

      @Schema(description = "계정 id", example = "3578e885-f55b-4476-925c-5838b58f4607")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID accountId
  ) implements BaseInput<RemoveProjectMemberUseCase> {

    @Override
    public RemoveProjectMemberUseCase toCommand() {
      return new RemoveProjectMemberUseCase(projectId, accountId);
    }
  }

}
