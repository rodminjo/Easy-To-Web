package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.project.domain.port.out.ProjectHistoryRepository;
import com.backend.easy_to_web.project.domain.usecase.SaveProjectHistoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Handler
public class SaveProjectHistoryHandler implements VoidUseCaseHandler<SaveProjectHistoryUseCase> {

  private final ProjectHistoryRepository projectHistoryRepository;


  @Override
  public Class<SaveProjectHistoryUseCase> getVoidUseCaseClass() {
    return SaveProjectHistoryUseCase.class;
  }

  @Override
  @Transactional
  public void handleVoid(SaveProjectHistoryUseCase useCase) {
    projectHistoryRepository.save(useCase.createHistory());
  }
}
