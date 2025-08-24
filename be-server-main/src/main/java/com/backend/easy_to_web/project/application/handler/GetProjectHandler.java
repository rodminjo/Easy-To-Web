package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.in.AccountGateway;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.model.Publish;
import com.backend.easy_to_web.project.domain.port.out.ProjectPublishRepository;
import com.backend.easy_to_web.project.domain.result.GetProjectResult;
import com.backend.easy_to_web.project.domain.result.GetProjectResult.ProjectMemberDto;
import com.backend.easy_to_web.project.domain.usecase.GetProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Handler
public class GetProjectHandler implements UseCaseHandler<GetProjectResult, GetProjectUseCase> {

  private final ProjectRepository projectRepository;
  private final ProjectPublishRepository projectPublishRepository;
  private final AccountGateway accountGateway;


  @Override
  public Class<GetProjectUseCase> getUseCaseClass() {
    return GetProjectUseCase.class;
  }

  @Override
  public GetProjectResult handle(GetProjectUseCase useCase) {
    UUID accountId = SecurityScopeUtils.getAccountId();
    Project project = projectRepository.getByProjectId(useCase.getProjectId());
    project.ensureHasPermission(accountId, ProjectPermission.READ_ONLY);

    // 프로젝트 참여자 id 매핑
    List<UUID> accountIds = project.getMemberIds();
    Map<UUID, ProjectPermission> memberIdPermissionMap = project.getMemberIdPermissionMap();

    // 프로젝트 참여자 닉네임 조회
    List<Account> accounts = accountGateway.findAllByIds(accountIds);
    List<ProjectMemberDto> list = accounts.stream().map(
        ac -> ProjectMemberDto.builder()
            .accountId(ac.getId())
            .email(ac.getEmail())
            .nickname(ac.getNickname())
            .profileUrl(ac.getProfileUrl())
            .permission(memberIdPermissionMap.get(ac.getId()))
            .build()
    ).toList();

    GetProjectResult result = new GetProjectResult(project, list);
    if (project.isPublished()){
      projectPublishRepository.findByProjectId(project.getId())
          .ifPresent(pub-> result.withPublishUrl(pub.getUrl()));
    }

    return result;
  }
}
