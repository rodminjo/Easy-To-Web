package com.backend.easy_to_web.project.infrastructure.persistence;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectEntity;
import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectMemberEntity;
import com.backend.easy_to_web.project.infrastructure.persistence.jpa.ProjectHistoryJpaRepository;
import com.backend.easy_to_web.project.infrastructure.persistence.jpa.ProjectJpaRepository;
import com.backend.easy_to_web.project.infrastructure.persistence.jpa.ProjectMemberJpaRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProjectRepositoryAdapter implements ProjectRepository {

  private final ProjectJpaRepository projectJpaRepository;
  private final ProjectMemberJpaRepository projectMemberJpaRepository;
  private final ProjectHistoryJpaRepository projectHistoryJpaRepository;

  @Override
  public Optional<Project> findByProjectId(UUID projectId) {
    Optional<ProjectEntity> find = projectJpaRepository.findById(projectId);
    Set<Member> members = new HashSet<>();
    if (find.isPresent()){
      List<ProjectMemberEntity> memberEntityList = projectMemberJpaRepository.findAllByProjectId(projectId);
      members.addAll(memberEntityList.stream().map(ProjectMemberEntity::toDomain).toList());
    }

    return find.map(entity -> entity.toDomain(members));
  }

  @Override
  public Project getByProjectId(UUID projectId) {
    return findByProjectId(projectId)
        .orElseThrow(()-> new CustomIllegalArgumentException(ExceptionMessage.PROJECT_NOT_FOUND));
  }

  @Override
  public Project save(Project project) {
    ProjectEntity entity = ProjectEntity.of(project);
    ProjectEntity savedEntity = projectJpaRepository.save(entity);

    Set<ProjectMemberEntity> exists = new HashSet<>(projectMemberJpaRepository.findAllByProjectId(savedEntity.getId()));
    Set<ProjectMemberEntity> updates = project.getMembers().stream()
        .map(ProjectMemberEntity::of)
        .collect(Collectors.toSet());

    updates.forEach(member -> member.updateProjectId(savedEntity.getId()));

    // 추가해야 할 멤버 (updates에는 있지만 exists에는 없는 경우)
    Set<ProjectMemberEntity> toAdd = new HashSet<>(updates);
//    toAdd.removeAll(exists);

    // 삭제해야 할 멤버 (exists에는 있지만 updates에는 없는 경우)
    Set<ProjectMemberEntity> toRemove = new HashSet<>(exists);
    toRemove.removeAll(updates);

    // 추가 및 삭제 반영
    projectMemberJpaRepository.deleteAll(toRemove);
    projectMemberJpaRepository.saveAll(toAdd);

    Set<Member> savedAll = projectMemberJpaRepository.findAllByProjectId(savedEntity.getId())
        .stream()
        .map(ProjectMemberEntity::toDomain)
        .collect(Collectors.toSet());

    return savedEntity.toDomain(new HashSet<>(savedAll));
  }

  @Override
  public List<Project> findAllByProjectIds(List<UUID> projectIds) {
    List<ProjectEntity> all = projectJpaRepository.findAllById(projectIds);
    List<Project> projects = new ArrayList<>();

    for (ProjectEntity entity : all) {
      Set<Member> members = projectMemberJpaRepository.findAllByProjectId(entity.getId())
          .stream()
          .map(ProjectMemberEntity::toDomain).collect(Collectors.toSet());
      projects.add(entity.toDomain(members));
    }

    return projects;
  }

  @Override
  public void delete(Project project) {
    projectHistoryJpaRepository.deleteAllByProjectId(project.getId());
    projectMemberJpaRepository.deleteAllByProjectId(project.getId());
    projectJpaRepository.deleteById(project.getId());
  }
}
