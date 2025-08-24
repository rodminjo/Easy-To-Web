package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.PublishProjectUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectPublishRest {
  @Schema(description = "프로젝트 게시 요청 데이터")
  public record ProjectPublishInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId,

      @Schema(description = "프로젝트 구조 Json 데이터", example = "{sectionKey:test, content: test, ...}")
      @NotNull(message = ValidationMessage.NOT_NULL)
      String content

  ) implements BaseInput<PublishProjectUseCase> {

    @Override
    public PublishProjectUseCase toCommand() {
      return new PublishProjectUseCase(projectId, content);
    }
  }

    @Schema(description = "프로젝트 게시 응답 데이터")
    public record ProjectPublishOutput(
        @Schema(description = "프로젝트 접속 url", example = "title-d2b59c4e")
        @NotNull(message = ValidationMessage.NOT_NULL)
        String url

    ) {

    }

}
