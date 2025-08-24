package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectThumbnailUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class UpdateProjectThumbnailRest {

  @Schema(description = "프로젝트 썸네일 변경 요청 데이터")
  public record UpdateProjectThumbnailInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID id,

      @Schema(description = "썸네일 파일 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID thumbnailFileId

  ) implements BaseInput<UpdateProjectThumbnailUseCase> {


    @Override
    public UpdateProjectThumbnailUseCase toCommand() {
      return new UpdateProjectThumbnailUseCase(id, thumbnailFileId);
    }
  }


}
