package com.backend.easy_to_web.project.infrastructure.controller;

import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.result.AccessProjectResult;
import com.backend.easy_to_web.project.domain.result.GetRecentProjectHistoryResult;
import com.backend.easy_to_web.project.domain.usecase.AccessProjectUseCase;
import com.backend.easy_to_web.project.domain.usecase.GetRecentProjectHistoryUseCase;
import com.backend.easy_to_web.project.domain.usecase.SaveProjectHistoryUseCase;
import com.backend.easy_to_web.socket.crdt.port.CRDTSyncProvider;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CRDTSyncProviderImpl implements CRDTSyncProvider {

  private final UseCasePublisher useCasePublisher;


  @Override
  public ProjectPermission hasAccessToProject(String accountId, String projectId) {
    UUID account = UUID.fromString(accountId);
    UUID project = UUID.fromString(projectId);
    AccessProjectUseCase command = new AccessProjectUseCase(account, project);
    AccessProjectResult result = useCasePublisher.publish(AccessProjectResult.class, command);
    return result.getPermission();
  }

  @Override
  public byte[] getRecentHistory(String projectId) {
    GetRecentProjectHistoryUseCase command = new GetRecentProjectHistoryUseCase(projectId);
    GetRecentProjectHistoryResult result = useCasePublisher.publish(GetRecentProjectHistoryResult.class, command);
    return result.getSnapshot();
  }

  @Override
  public void saveHistory(String roomName, byte[] snapshot, Set<UUID> editors) {
    SaveProjectHistoryUseCase command = new SaveProjectHistoryUseCase(roomName, snapshot, editors);
    useCasePublisher.publish(command);
  }
}
