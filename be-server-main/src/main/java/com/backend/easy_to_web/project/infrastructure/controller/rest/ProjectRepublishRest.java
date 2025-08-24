package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.RepublishProjectUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectRepublishRest {
  @Schema(description = "프로젝트 게시 최신화 요청 데이터")
  public record ProjectRepublishInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId,

      @Schema(description = "프로젝트 구조 Json 데이터", example = "{sectionKey:test, content: test, ...}")
      @NotNull(message = ValidationMessage.NOT_NULL)
      String content

  ) implements BaseInput<RepublishProjectUseCase> {

    @Override
    public RepublishProjectUseCase toCommand() {
      return new RepublishProjectUseCase(projectId, content);
    }
  }


}
