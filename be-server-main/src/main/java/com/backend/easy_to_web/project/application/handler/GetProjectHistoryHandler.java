package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.account.domain.port.in.AccountGateway;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.result.GetProjectHistoryResult;
import com.backend.easy_to_web.project.domain.usecase.GetProjectHistoryUseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.History;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectHistoryRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Handler
public class GetProjectHistoryHandler implements UseCaseHandler<GetProjectHistoryResult, GetProjectHistoryUseCase> {

  private final ProjectRepository projectRepository;
  private final ProjectHistoryRepository projectHistoryRepository;
  private final AccountGateway accountGateway;


  @Override
  public Class<GetProjectHistoryUseCase> getUseCaseClass() {
    return GetProjectHistoryUseCase.class;
  }

  @Override
  public GetProjectHistoryResult handle(GetProjectHistoryUseCase useCase) {
    // 프로젝트 유효성 검사
    UUID accountId = SecurityScopeUtils.getAccountId();
    UUID projectId = useCase.getProjectId();
    Project project = projectRepository.getByProjectId(projectId);
    project.ensureHasPermission(accountId, ProjectPermission.READ_ONLY);

    // 프로젝트 참여자 id 매핑
    List<UUID> accountIds = project.getMembers().stream()
        .map(Member::getAccountId)
        .toList();

    // 프로젝트 참여자 닉네임 조회
    List<Account> accounts = accountGateway.findAllByIds(accountIds);
    Map<UUID, String> nicknameMap = accounts.stream()
        .collect(Collectors.toMap(Account::getId, Account::getNickname));

    // 히스토리 조회
    History history = projectHistoryRepository.findByProjectIdAndId(projectId, useCase.getHistoryId())
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.PROJECT_HISTORY_NOT_FOUND));

    List<String> nicknames = history.editorToNickname(nicknameMap);
    return new GetProjectHistoryResult(history.getContent(), history.getEditTime(), nicknames);
  }
}
