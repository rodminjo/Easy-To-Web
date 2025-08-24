package com.backend.easy_to_web.socket.crdt.port;

import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import java.util.Set;
import java.util.UUID;

public interface CRDTSyncProvider {

  ProjectPermission hasAccessToProject(String accountId, String projectId);

  byte[] getRecentHistory(String projectId);

  void saveHistory(String roomName, byte[] snapshot, Set<UUID> editors);

}
