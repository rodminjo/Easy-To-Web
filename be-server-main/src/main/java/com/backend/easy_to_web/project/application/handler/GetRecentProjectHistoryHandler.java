package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.project.domain.port.out.ProjectHistoryRepository;
import com.backend.easy_to_web.project.domain.result.GetRecentProjectHistoryResult;
import com.backend.easy_to_web.project.domain.usecase.GetRecentProjectHistoryUseCase;
import com.backend.easy_to_web.project.domain.model.History;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Handler
public class GetRecentProjectHistoryHandler implements UseCaseHandler<GetRecentProjectHistoryResult, GetRecentProjectHistoryUseCase> {

  private final ProjectHistoryRepository projectHistoryRepository;


  @Override
  public Class<GetRecentProjectHistoryUseCase> getUseCaseClass() {
    return GetRecentProjectHistoryUseCase.class;
  }

  @Override
  @Transactional
  public GetRecentProjectHistoryResult handle(GetRecentProjectHistoryUseCase useCase) {
    UUID projectId = UUID.fromString(useCase.getProjectId());

    Optional<History> history = projectHistoryRepository.findRecentOne(projectId);
    byte[] snapshot = history.isPresent() ? history.get().getContent() : new byte[1];

    return new GetRecentProjectHistoryResult(snapshot);
  }
}
