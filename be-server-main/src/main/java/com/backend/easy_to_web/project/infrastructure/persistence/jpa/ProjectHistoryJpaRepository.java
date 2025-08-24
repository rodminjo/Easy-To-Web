package com.backend.easy_to_web.project.infrastructure.persistence.jpa;

import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectHistoryEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectHistoryJpaRepository extends JpaRepository<ProjectHistoryEntity, Long> {

  Optional<ProjectHistoryEntity> findFirstByProjectIdOrderByEditTimeDesc(UUID projectId);

  Page<ProjectHistoryEntity> findAllByProjectIdOrderByEditTimeDesc(UUID projectId, Pageable pageable);

  Optional<ProjectHistoryEntity> findByProjectIdAndId(UUID projectId, long id);

  void deleteAllByProjectId(UUID projectId);
}
