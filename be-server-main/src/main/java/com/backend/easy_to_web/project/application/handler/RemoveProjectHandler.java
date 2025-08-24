package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.usecase.RemoveProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class RemoveProjectHandler implements VoidUseCaseHandler<RemoveProjectUseCase> {

  private final ProjectRepository projectRepository;


  @Override
  public Class<RemoveProjectUseCase> getVoidUseCaseClass() {
    return RemoveProjectUseCase.class;
  }

  @Override
  @Transactional
  public void handleVoid(RemoveProjectUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Project project = projectRepository.getByProjectId(useCase.getProjectId());
    project.ensureHasPermission(accountId, ProjectPermission.OWNER);
    projectRepository.delete(project);
  }
}
