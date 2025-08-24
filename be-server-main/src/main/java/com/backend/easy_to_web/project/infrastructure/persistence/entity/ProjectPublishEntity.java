package com.backend.easy_to_web.project.infrastructure.persistence.entity;

import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import com.backend.easy_to_web.project.domain.model.Publish;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "project_publish")
public class ProjectPublishEntity extends BaseEntity {


  @Id
  @Comment("프로젝트 ID (Project와 1:1 매핑)")
  private UUID projectId;

  @Column
  @Comment("공개 URL")
  private String url;

  @Column(columnDefinition = "TEXT")
  @Comment("출판된 프로젝트 JSON 데이터")
  private String json;

  @Column
  @Comment("페이지 조회수")
  private int viewCount;


  public Publish toDomain(){
    return Publish.builder()
        .projectId(projectId)
        .url(url)
        .content(json)
        .viewCount(viewCount)
        .createdBy(getCreatedBy())
        .createdDate(getCreatedDate())
        .modifiedDate(getModifiedDate())
        .updatedBy(getUpdatedBy())
        .build();
  }

  public static ProjectPublishEntity of(Publish domain) {
    return new ProjectPublishEntity(domain.getProjectId(), domain.getUrl(), domain.getContent(), domain.getViewCount());

  }
}
