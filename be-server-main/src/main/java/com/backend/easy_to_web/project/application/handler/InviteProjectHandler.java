package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.in.AccountGateway;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.port.CodeGenerator;
import com.backend.easy_to_web.common.application.port.RedisRepository;
import com.backend.easy_to_web.config.properties.ProjectInviteProperties;
import com.backend.easy_to_web.project.domain.usecase.InviteProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Invitation;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.project.infrastructure.external.ProjectInvitationLinkGenerator;
import com.backend.easy_to_web.project.infrastructure.external.ProjectInviteEmailSender;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Handler
public class InviteProjectHandler implements VoidUseCaseHandler<InviteProjectUseCase> {

  private final ProjectRepository projectRepository;
  private final RedisRepository redisRepository;
  private final ProjectInviteEmailSender emailSender;
  private final CodeGenerator codeGenerator;
  private final ProjectInvitationLinkGenerator linkGenerator;

  private final AccountGateway accountGateway;


  @Override
  public Class<InviteProjectUseCase> getVoidUseCaseClass() {
    return InviteProjectUseCase.class;
  }

  @Override
  public void handleVoid(InviteProjectUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Project project = projectRepository.getByProjectId(useCase.getProjectId());
    project.ensureHasPermission(accountId, ProjectPermission.ADMIN);

    Account target = accountGateway.getByEmail(useCase.getEmail());
    if (project.findMember(target.getId()).isPresent()){
      throw new CustomIllegalArgumentException(ExceptionMessage.PROJECT_MEMBER_ALREADY_EXIST);
    }

    UUID projectId = project.getId();
    String generateCode = codeGenerator.generateCode(20);
    LocalDateTime localDateTime = LocalDateTime.now().plusDays(3);
    Invitation invite = new Invitation(project.getId(), target.getId(), generateCode, localDateTime);

    String url = linkGenerator.generate(List.of(projectId.toString(), generateCode));
    emailSender.send(target.getEmail(), Map.of("inviteLink", url));
    saveInviteInCache(invite);
  }


  // Redis 저장
  private void saveInviteInCache(Invitation invite) {
    redisRepository.setWithExpired(
        Invitation.getKey(invite.getProjectId(), invite.getAccountId()),
        invite,
        invite.calDurationMillis()
    );
  }

}
