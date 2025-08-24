package com.backend.easy_to_web.project.infrastructure.persistence.entity;

import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "project_member")
public class ProjectMemberEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("프로젝트 멤버 ID")
  private long id;

  @Column(nullable = false)
  @Comment("참여한 프로젝트의 ID")
  private UUID projectId;

  @Column(nullable = false)
  @Comment("멤버의 계정 ID (Account 참조)")
  private UUID accountId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Comment("해당 프로젝트에서의 권한 (READ_ONLY, EDIT, ADMIN)")
  private ProjectPermission permission;


  public Member toDomain(){
    return Member.builder()
        .id(id)
        .projectId(projectId)
        .accountId(accountId)
        .permission(permission)
        .createdDate(getCreatedDate())
        .createdBy(getCreatedBy())
        .modifiedDate(getModifiedDate())
        .updatedBy(getUpdatedBy())
        .build();
  }

  public static ProjectMemberEntity of(Member domain) {
    return new ProjectMemberEntity(domain.getId(), domain.getProjectId(), domain.getAccountId(), domain.getPermission());
  }

  public void updateProjectId(UUID projectId){
    this.projectId = projectId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProjectMemberEntity that = (ProjectMemberEntity) o;
    return Objects.equals(projectId, that.projectId) && Objects.equals(accountId, that.accountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, accountId);
  }


}
