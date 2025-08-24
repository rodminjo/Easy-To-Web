package com.backend.easy_to_web.project.infrastructure.persistence;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.port.out.ProjectMemberRepository;
import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectMemberEntity;
import com.backend.easy_to_web.project.infrastructure.persistence.jpa.ProjectMemberJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProjectMemberRepositoryAdapter implements ProjectMemberRepository {

  private final ProjectMemberJpaRepository projectMemberJpaRepository;

  @Override
  public List<Member> findAllByAccountId(UUID accountId) {
    return projectMemberJpaRepository.findAllByAccountId(accountId).stream()
        .map(ProjectMemberEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<Member> findByAccountIdAndProjectId(UUID accountId, UUID projectId) {
    return projectMemberJpaRepository.findByAccountIdAndProjectId(accountId, projectId)
        .map(ProjectMemberEntity::toDomain);
  }

  @Override
  public Member getByAccountIdAndProjectId(UUID accountId, UUID projectId) {
    return findByAccountIdAndProjectId(accountId, projectId)
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.PROJECT_MEMBER_NOT_FOUND));
  }

  @Override
  public Member save(Member member) {
    ProjectMemberEntity memberEntity = ProjectMemberEntity.of(member);
    return projectMemberJpaRepository.save(memberEntity).toDomain();
  }

  @Override
  public void delete(Member member) {
    ProjectMemberEntity memberEntity = ProjectMemberEntity.of(member);
    projectMemberJpaRepository.delete(memberEntity);
  }

}
