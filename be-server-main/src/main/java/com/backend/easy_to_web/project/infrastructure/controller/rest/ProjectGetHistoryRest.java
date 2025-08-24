package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseInput;
import com.backend.easy_to_web.project.domain.usecase.GetProjectHistoryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProjectGetHistoryRest {
  @Schema(description = "프로젝트 히스토리 단건 조회 입력 데이터")
  public record ProjectGetHistoryInput(
      @Schema(description = "프로젝트 id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId,

      @Schema(description = "히스토리 id", example = "1")
      @NotNull           // null 방지
      @Positive
      long historyId

  ) implements BaseInput<GetProjectHistoryUseCase> {


    @Override
    public GetProjectHistoryUseCase toCommand() {
      return new GetProjectHistoryUseCase(this.projectId, this.historyId);
    }

  }

  @Schema(description = "프로젝트 히스토리 단건 조회 응답 데이터")
  public record ProjectGetHistoryOutput(

      @Schema(description = "Base64 인코딩된 히스토리 데이터", example = "VGhpcyBpcyBhIHNhbXBsZSBieXRlIGFycmF5")
      byte[] content,

      @Schema(description = "프로젝트 히스토리 시간", example = "2025-04-04T16:30:00")
      LocalDateTime editTime,

      @Schema(description = "프로젝트 히스토리 기여자 목록", example = "['editor1','editor2']")
      List<String> editor


  ) {
  }
}
