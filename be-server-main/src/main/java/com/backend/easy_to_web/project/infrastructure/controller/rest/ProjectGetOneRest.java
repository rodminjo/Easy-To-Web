package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.result.GetProjectResult;
import com.backend.easy_to_web.project.domain.result.GetProjectResult.ProjectMemberDto;
import com.backend.easy_to_web.project.domain.usecase.GetProjectUseCase;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import com.backend.easy_to_web.utils.MapperUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProjectGetOneRest {

  @Schema(description = "프로젝트 단건 조회 요청 데이터")
  public record ProjectGetOneInput(
      @Schema(description = "프로젝트 id", example = "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId
  ) implements BaseInput<GetProjectUseCase> {


    @Override
    public GetProjectUseCase toCommand() {
      return new GetProjectUseCase(this.projectId);

    }
  }


  @Schema(description = "프로젝트 단건 조회 응답 데이터")
  public record ProjectGetOneOutput(
      @Schema(description = "프로젝트 id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
      UUID id,

      @Schema(description = "프로젝트 이름", example = "프로젝트명")
      String title,

      @Schema(description = "프로젝트 설명", example = "프로젝트입니다.")
      String description,

      @Schema(description = "프로젝트 상태", example = "CLOSED, OPEN")
      ProjectStatus status,

      @Schema(description = "프로젝트 생성일시", example = MapperUtils.DATE_TIME_FORMATTER_PATTERN)
      LocalDateTime createDate,

      @Schema(description = "프로젝트 게시 url", example = "/3fa85f64-5717-4562-b3fc-2c963f66afa6")
      String publishUrl,

      @Schema(
          description = "프로젝트 참여자 목록",
          example = "[{accountId: 3578e885-f55b-4476-925c-5838b58f4607, email: example@naver.com, nickname: 닉네임, profileUrl: link, permission: OWNER}]"
      )
      List<ProjectMemberDto> members

  ) {

    public static ProjectGetOneOutput fromResult(GetProjectResult result){
      return new ProjectGetOneOutput(
          result.getId(), result.getTitle(), result.getDescription(),
          result.getStatus(), result.getCreateDate(), result.getPublishUrl(), result.getMembers()
      );
    }

  }
}
