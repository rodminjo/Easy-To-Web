package com.backend.easy_to_web.project.infrastructure.persistence.jpa;

import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectMemberEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberEntity, Long> {

  List<ProjectMemberEntity> findAllByProjectId(UUID projectId);

  List<ProjectMemberEntity> findAllByAccountId(UUID accountId);

  Optional<ProjectMemberEntity> findByAccountIdAndProjectId(UUID accountId, UUID projectId);

  void deleteAllByProjectId(UUID projectId);

}
