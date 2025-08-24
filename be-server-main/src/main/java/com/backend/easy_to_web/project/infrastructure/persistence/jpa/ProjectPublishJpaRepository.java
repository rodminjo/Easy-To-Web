package com.backend.easy_to_web.project.infrastructure.persistence.jpa;

import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectPublishEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectPublishJpaRepository extends JpaRepository<ProjectPublishEntity, UUID> {

  Optional<ProjectPublishEntity> findByUrl(String url);

  Optional<ProjectPublishEntity> findByProjectId(UUID projectId);

  void deleteByProjectId(UUID projectId);
}
