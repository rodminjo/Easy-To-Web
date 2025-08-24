package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.port.RedisRepository;
import com.backend.easy_to_web.project.domain.result.AcceptProjectInvitationResult;
import com.backend.easy_to_web.project.domain.usecase.AcceptProjectInvitationUseCase;
import com.backend.easy_to_web.project.domain.model.Invitation;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectMemberRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
@Handler
public class AcceptProjectInvitationHandler implements UseCaseHandler<AcceptProjectInvitationResult, AcceptProjectInvitationUseCase> {

  private final ProjectMemberRepository projectMemberRepository;
  private final RedisRepository redisRepository;


  @Override
  public Class<AcceptProjectInvitationUseCase> getUseCaseClass() {
    return AcceptProjectInvitationUseCase.class;
  }

  @Override
  @Transactional
  public AcceptProjectInvitationResult handle(AcceptProjectInvitationUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    UUID projectId = useCase.getProjectId();

    Invitation data = redisRepository.getAndDelete(Invitation.getKey(projectId, accountId),
        Invitation.class);
    if (ObjectUtils.isEmpty(data) || !data.isValidCode(useCase.getCode())) {
      throw new CustomIllegalArgumentException(ExceptionMessage.CERTIFICATION_FAILED);
    }

    Member newMember = Member.create(projectId, accountId, ProjectPermission.READ_ONLY);
    projectMemberRepository.save(newMember);

    return new AcceptProjectInvitationResult(projectId);
  }
}
