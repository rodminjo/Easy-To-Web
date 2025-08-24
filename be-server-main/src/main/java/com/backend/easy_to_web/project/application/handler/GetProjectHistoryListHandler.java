package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.account.domain.port.in.AccountGateway;
import com.backend.easy_to_web.account.domain.port.out.AccountRepository;
import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.project.domain.port.out.ProjectHistoryRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.project.domain.result.GetProjectHistoryListResult;
import com.backend.easy_to_web.project.domain.result.GetProjectHistoryListResult.HistoryDto;
import com.backend.easy_to_web.project.domain.usecase.GetProjectHistoryListUseCase;
import com.backend.easy_to_web.project.domain.model.History;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@RequiredArgsConstructor
@Handler
public class GetProjectHistoryListHandler implements UseCaseHandler<GetProjectHistoryListResult, GetProjectHistoryListUseCase> {

  private final ProjectRepository projectRepository;
  private final ProjectHistoryRepository projectHistoryRepository;
  private final AccountGateway accountGateway;


  @Override
  public Class<GetProjectHistoryListUseCase> getUseCaseClass() {
    return GetProjectHistoryListUseCase.class;
  }

  @Override
  public GetProjectHistoryListResult handle(GetProjectHistoryListUseCase useCase) {
    // 프로젝트 유효성 검사
    UUID accountId = SecurityScopeUtils.getAccountId();
    UUID projectId = useCase.getProjectId();
    Project project = projectRepository.getByProjectId(projectId);
    project.ensureHasPermission(accountId, ProjectPermission.READ_ONLY);

    // 프로젝트 참여자 id 매핑
    List<UUID> accountIds = project.getMemberIds();

    // 프로젝트 참여자 닉네임 조회
    List<Account> accounts = accountGateway.findAllByIds(accountIds);
    Map<UUID, String> nicknameMap = accounts.stream()
        .collect(Collectors.toMap(Account::getId, Account::getNickname));

    // 목록 조회 및 반환
    Page<History> all = projectHistoryRepository.findAll(projectId, useCase.getPageable());
    long total = all.getTotalElements();
    List<HistoryDto> historyList = all.stream()
        .map(ph -> HistoryDto.toDto(ph, nicknameMap))
        .toList();

    return new GetProjectHistoryListResult(total, historyList);
  }
}
