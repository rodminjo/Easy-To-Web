package com.backend.easy_to_web.project.infrastructure.persistence;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.project.domain.model.History;
import com.backend.easy_to_web.project.domain.port.out.ProjectHistoryRepository;
import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectHistoryEntity;
import com.backend.easy_to_web.project.infrastructure.persistence.jpa.ProjectHistoryJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProjectHistoryRepositoryAdapter implements ProjectHistoryRepository {

  private final ProjectHistoryJpaRepository projectHistoryJpaRepository;


  @Override
  public Optional<History> findRecentOne(UUID projectId) {
    return projectHistoryJpaRepository.findFirstByProjectIdOrderByEditTimeDesc(projectId)
        .map(ProjectHistoryEntity::toDomain);
  }

  @Override
  public History getRecentOne(UUID projectId) {
    return findRecentOne(projectId)
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.PROJECT_HISTORY_NOT_FOUND));
  }

  @Override
  public History save(History history) {
    ProjectHistoryEntity entity = ProjectHistoryEntity.of(history);
    return projectHistoryJpaRepository.save(entity).toDomain();
  }

  @Override
  public Page<History> findAll(UUID projectId, Pageable pageable) {
    Page<ProjectHistoryEntity> list
        = projectHistoryJpaRepository.findAllByProjectIdOrderByEditTimeDesc(projectId, pageable);

    return list.map(ProjectHistoryEntity::toDomain);
  }

  @Override
  public Optional<History> findByProjectIdAndId(UUID projectId, long id) {
    return projectHistoryJpaRepository.findByProjectIdAndId(projectId, id)
        .map(ProjectHistoryEntity::toDomain);
  }
}
