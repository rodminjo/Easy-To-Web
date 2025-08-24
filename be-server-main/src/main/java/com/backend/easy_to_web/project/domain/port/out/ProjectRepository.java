package com.backend.easy_to_web.project.domain.port.out;

import com.backend.easy_to_web.project.domain.model.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {

  Optional<Project> findByProjectId(UUID projectId);

  Project getByProjectId(UUID projectId);

  Project save(Project project);

  List<Project> findAllByProjectIds(List<UUID> projectIds);

  void delete(Project project);

}
