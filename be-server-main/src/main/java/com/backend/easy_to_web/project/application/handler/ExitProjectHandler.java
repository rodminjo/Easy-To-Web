package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.project.domain.port.out.ProjectMemberRepository;
import com.backend.easy_to_web.project.domain.usecase.ExitProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Handler
public class ExitProjectHandler implements VoidUseCaseHandler<ExitProjectUseCase> {

  private final ProjectMemberRepository projectMemberRepository;


  @Override
  public Class<ExitProjectUseCase> getVoidUseCaseClass() {
    return ExitProjectUseCase.class;
  }

  @Override
  public void handleVoid(ExitProjectUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Member member = projectMemberRepository.getByAccountIdAndProjectId(accountId, useCase.getProjectId());
    member.ensureNotOwner();
    projectMemberRepository.delete(member);
  }
}
