package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectUseCase;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectUpdateRest {

  @Schema(description = "프로젝트 정보 변경 요청 데이터")
  public record ProjectUpdateInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID id,

      @Schema(description = "프로젝트 이름", example = "프로젝트명")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String title,

      @Schema(description = "프로젝트 설명", example = "프로젝트입니다.")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String description

  ) implements BaseInput<UpdateProjectUseCase> {


    @Override
    public UpdateProjectUseCase toCommand() {
      return new UpdateProjectUseCase(id, title, description);
    }
  }


}
