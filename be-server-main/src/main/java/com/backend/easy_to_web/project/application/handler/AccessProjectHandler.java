package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.project.domain.port.out.ProjectMemberRepository;
import com.backend.easy_to_web.project.domain.result.AccessProjectResult;
import com.backend.easy_to_web.project.domain.usecase.AccessProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Handler
public class AccessProjectHandler implements UseCaseHandler<AccessProjectResult, AccessProjectUseCase> {

  private final ProjectMemberRepository projectMemberRepository;


  @Override
  public Class<AccessProjectUseCase> getUseCaseClass() {
    return AccessProjectUseCase.class;
  }

  @Override
  public AccessProjectResult handle(AccessProjectUseCase useCase) {
    Member member = projectMemberRepository
        .findByAccountIdAndProjectId(useCase.getAccountId(), useCase.getProjectId())
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.PROJECT_ACCESS_DENIED));

    ProjectPermission permission = member.getPermission();
    return new AccessProjectResult(permission);
  }

}
