package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetProjectListResult implements Result {

  private final Map<ProjectPermission, List<ProjectInfoDto>> projectInfos;

  public GetProjectListResult() {
    this.projectInfos = new HashMap<>();
    for (ProjectPermission permission : ProjectPermission.values()){
      projectInfos.put(permission, new ArrayList<>());
    }
  }

  @Builder
  public record ProjectInfoDto(
      UUID id,
      String thumbnailUrl,
      String title,
      String description,
      ProjectStatus status,
      int memberCount,
      LocalDateTime joinedDate
  ){

  }
}
