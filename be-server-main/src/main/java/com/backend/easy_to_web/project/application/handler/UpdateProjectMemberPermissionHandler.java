package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectMemberPermissionUseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectMemberRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Handler
public class UpdateProjectMemberPermissionHandler implements VoidUseCaseHandler<UpdateProjectMemberPermissionUseCase> {

  private final ProjectRepository projectRepository;
  private final ProjectMemberRepository projectMemberRepository;


  @Override
  public Class<UpdateProjectMemberPermissionUseCase> getVoidUseCaseClass() {
    return UpdateProjectMemberPermissionUseCase.class;
  }

  @Override
  public void handleVoid(UpdateProjectMemberPermissionUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Project project = projectRepository.getByProjectId(useCase.getProjectId());
    project.ensureHasPermission(accountId, ProjectPermission.ADMIN);

    Member member = projectMemberRepository.getByAccountIdAndProjectId(useCase.getAccountId(), useCase.getProjectId());
    member.updatePermission(project.getMember(accountId).getPermission(), useCase.getPermission());
    projectMemberRepository.save(member);
  }
}
