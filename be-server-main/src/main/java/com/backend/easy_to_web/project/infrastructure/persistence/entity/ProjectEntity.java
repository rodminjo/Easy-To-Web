package com.backend.easy_to_web.project.infrastructure.persistence.entity;

import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "project")
public class ProjectEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Comment("프로젝트 ID (UUID)")
  private UUID id;

  @Comment("썸네일 파일 ID (File 테이블의 UUID)")
  @Column
  private UUID thumbnailFileId;

  @Comment("프로젝트 이름")
  @Column
  private String title;

  @Comment("프로젝트 설명")
  @Column
  private String description;

  @Enumerated(EnumType.STRING)
  @Column
  @Comment("프로젝트 상태 (예: OPEN, CLOSED)")
  private ProjectStatus status;

  public Project toDomain(Set<Member> members){
    return Project.builder()
        .id(this.id)
        .thumbnailFileId(this.thumbnailFileId)
        .title(this.title)
        .description(this.description)
        .status(this.status)
        .members(members)
        .createdDate(getCreatedDate())
        .createdBy(getCreatedBy())
        .modifiedDate(getModifiedDate())
        .updatedBy(getUpdatedBy())
        .build();
  }

  public static ProjectEntity of(Project domain){
    return new ProjectEntity(domain.getId(), domain.getThumbnailFileId(), domain.getTitle(), domain.getDescription(), domain.getStatus());
  }
}
