package com.backend.easy_to_web.project.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UpdateProjectMemberPermissionUseCase implements UseCase {

  private final UUID projectId;
  private final UUID accountId;
  private final ProjectPermission permission;

}
