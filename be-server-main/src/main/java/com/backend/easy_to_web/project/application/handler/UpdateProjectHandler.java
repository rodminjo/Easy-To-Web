package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Handler
public class UpdateProjectHandler implements VoidUseCaseHandler<UpdateProjectUseCase> {

  private final ProjectRepository projectRepository;


  @Override
  public Class<UpdateProjectUseCase> getVoidUseCaseClass() {
    return UpdateProjectUseCase.class;
  }

  @Override
  public void handleVoid(UpdateProjectUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Project project = projectRepository.getByProjectId(useCase.getId());
    project.ensureHasPermission(accountId, ProjectPermission.ADMIN);

    project.updateInfo(useCase.getTitle(), useCase.getDescription());
    projectRepository.save(project);
  }
}
