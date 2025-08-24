package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.RemoveProjectUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectRemoveRest {
  @Schema(description = "프로젝트 삭제 요청 데이터")
  public record ProjectRemoveInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId

  ) implements BaseInput<RemoveProjectUseCase> {

    @Override
    public RemoveProjectUseCase toCommand() {
      return new RemoveProjectUseCase(projectId);
    }
  }

}
