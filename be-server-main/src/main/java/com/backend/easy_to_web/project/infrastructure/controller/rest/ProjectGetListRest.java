package com.backend.easy_to_web.project.infrastructure.controller.rest;

import com.backend.easy_to_web.project.domain.result.GetProjectListResult.ProjectInfoDto;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

public class ProjectGetListRest {
  @Schema(description = "프로젝트 목록 조회 응답 데이터")
  public record ProjectGetListOutput(
      @Schema(
          description = "접근권한별 프로젝트 목록",
          example = "{READ_ONLY:[], EDIT:[], ADMIN:[], OWNER:[]}"
      )
      Map<ProjectPermission, List<ProjectInfoDto>> projectInfos

  ) {

  }
}
