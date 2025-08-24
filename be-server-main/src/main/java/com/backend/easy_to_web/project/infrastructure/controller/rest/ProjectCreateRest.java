package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.CreateProjectUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectCreateRest {
  @Schema(description = "프로젝트 생성 데이터")
  public record ProjectCreateInput(
      @Schema(description = "프로젝트 이름", example = "이지웹")
      @NotBlank(message = ValidationMessage.STRING_REQUIRED)
      String title,

      @Schema(description = "프로젝트 설명", example = "웹사이트")
      @NotNull(message = ValidationMessage.NOT_NULL)
      String description


  ) implements BaseInput<CreateProjectUseCase> {


    @Override
    public CreateProjectUseCase toCommand() {
      return new CreateProjectUseCase(title, description);
    }
  }

  @Schema(description = "프로젝트 생성 응답 데이터")
  public record ProjectCreateOutput(
      @Schema(description = "프로젝트 ID", example = "UUID")
      UUID projectId

  ) {

  }
}
