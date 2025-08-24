package com.backend.easy_to_web.project.infrastructure.persistence.entity;

import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import com.backend.easy_to_web.project.domain.model.History;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "project_history")
public class ProjectHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("프로젝트 히스토리 ID")
  private long id;


  @Column(nullable = false)
  @Comment("관련된 프로젝트의 ID")
  private UUID projectId; // 관련된 프로젝트의 ID

  @Column(columnDefinition = "BYTEA", nullable = false)
  @Comment("편집된 내용 (바이트 배열)")
  private byte[] content; // 편집된 내용 (바이트 배열로 저장)

  @Column(nullable = false)
  @Comment("편집된 시간")
  private LocalDateTime editTime; // 편집된 시간

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "project_history_editor", joinColumns = @JoinColumn(name = "history_id"))
  @Column(name = "editors")
  @Comment("편집한 사용자 ID 집합")
  private Set<UUID> editors = new HashSet<>();


  public History toDomain(){
    return History.builder()
        .id(id)
        .content(content)
        .projectId(projectId)
        .editTime(editTime)
        .editor(editors)
        .build();
  }

  public static ProjectHistoryEntity of(History domain) {
    return new ProjectHistoryEntity(
        domain.getId(),
        domain.getProjectId(),
        domain.getContent(),
        domain.getEditTime(),
        new HashSet<>(domain.getEditor())
    );
  }
}
