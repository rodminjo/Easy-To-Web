package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.CustomPageable;
import com.backend.easy_to_web.common.infrastructure.controller.ValidationMessage;
import com.backend.easy_to_web.project.domain.result.GetProjectHistoryListResult.HistoryDto;
import com.backend.easy_to_web.project.domain.usecase.GetProjectHistoryListUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class ProjectGetHistoryListRest {
  @Schema(description = "프로젝트 히스토리 목록 입력 데이터")
  public record ProjectGetHistoryListInput(
      @Schema(description = "프로젝트 id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
      @NotNull(message = ValidationMessage.NOT_NULL)
      UUID projectId


  ) {

    public GetProjectHistoryListUseCase toCommand(CustomPageable pageable) {
      return new GetProjectHistoryListUseCase(projectId, pageable.toPageable());
    }
  }

  @Schema(description = "프로젝트 히스토리 목록 응답 데이터")
  public record ProjectGetHistoryListOutput(

      @Schema(description = "전체 갯수", example = "10")
      long totalCount,

      @Schema(description = "프로젝트 히스토리 목록", example = "[{id: 0, editTime: 'yyyy-MM-dd'T'HH:mm:ss' , ['editor1','editor2]}]")
      List<HistoryDto> projectHistories
  ) {
  }
}
