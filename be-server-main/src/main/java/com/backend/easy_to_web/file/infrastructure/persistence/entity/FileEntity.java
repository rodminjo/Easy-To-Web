package com.backend.easy_to_web.file.infrastructure.persistence.entity;

import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.model.FileUseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "file")
public class FileEntity extends BaseEntity {

  @Id
  @Comment("파일 ID (UUID)")
  private UUID id;

  @Column(nullable = false)
  @Comment("업로드 시 원본 파일명")
  private String originalName;

  @Column(nullable = false)
  @Comment("서버에 저장된 파일명 (UUID 기반)")
  private String savedName;

  @Column(nullable = false)
  @Comment("파일이 저장된 상대 경로 또는 전체 경로")
  private String path;

  @Column(nullable = false)
  @Comment("파일 크기 (바이트 단위)")
  private long size;

  @Enumerated(EnumType.STRING)
  @Column
  @Comment("파일의 콘텐츠 타입 (예: IMAGE, VIDEO, PDF)")
  private ContentType contentType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Comment("파일 사용 목적 (예: PROFILE, DOCUMENT 등)")
  private FileUseType useType;

  @Column
  @Comment("인코딩 등의 이유로 원본 파일 ID가 존재할 경우 참조")
  private UUID originalFileId;



  public File toDomain() {
    return File.builder()
        .id(id)
        .originalName(originalName)
        .savedName(savedName)
        .path(path)
        .size(size)
        .contentType(contentType)
        .useType(useType)
        .originalFileId(originalFileId)
        .createdDate(getCreatedDate())
        .modifiedDate(getModifiedDate())
        .createdBy(getCreatedBy())
        .updatedBy(getUpdatedBy())
        .build();
  }

  public static FileEntity of(File domain) {
    return FileEntity.builder()
        .id(domain.getId())
        .originalName(domain.getOriginalName())
        .savedName(domain.getSavedName())
        .path(domain.getPath())
        .size(domain.getSize())
        .contentType(domain.getContentType())
        .useType(domain.getUseType())
        .originalFileId(domain.getOriginalFileId())
        .build();

  }
}
