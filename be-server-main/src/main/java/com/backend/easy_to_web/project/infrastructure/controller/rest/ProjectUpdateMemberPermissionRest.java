package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectMemberPermissionUseCase;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProjectUpdateMemberPermissionRest {
  @Schema(description = "프로젝트 멤버 권한 변경 요청 데이터")
  public record ProjectUpdateMemberPermissionInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId,

      @Schema(description = "계정 id", example = "3578e885-f55b-4476-925c-5838b58f4607")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID accountId,

      @Schema(description = "프로젝트 권한", example = "EDIT")
      @NotNull(message = ValidationMessage.NOT_NULL)
      ProjectPermission permission

  ) implements BaseInput<UpdateProjectMemberPermissionUseCase> {

    @Override
    public UpdateProjectMemberPermissionUseCase toCommand() {
      return new UpdateProjectMemberPermissionUseCase(projectId, accountId, permission);
    }
  }

}
