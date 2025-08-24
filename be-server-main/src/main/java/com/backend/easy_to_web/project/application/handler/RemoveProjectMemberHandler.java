package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.usecase.RemoveProjectMemberUseCase;
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
public class RemoveProjectMemberHandler implements VoidUseCaseHandler<RemoveProjectMemberUseCase> {

  private final ProjectRepository projectRepository;
  private final ProjectMemberRepository projectMemberRepository;


  @Override
  public Class<RemoveProjectMemberUseCase> getVoidUseCaseClass() {
    return RemoveProjectMemberUseCase.class;
  }

  @Override
  public void handleVoid(RemoveProjectMemberUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Project project = projectRepository.getByProjectId(useCase.getProjectId());
    project.ensureHasPermission(accountId, ProjectPermission.ADMIN);

    Member member = projectMemberRepository.getByAccountIdAndProjectId(useCase.getAccountId(), useCase.getProjectId());
    member.ensureDeletePermissionIsValid(project.getMember(accountId).getPermission());
    projectMemberRepository.delete(member);
  }
}
