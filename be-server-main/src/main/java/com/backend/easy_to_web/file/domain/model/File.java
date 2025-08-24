package com.backend.easy_to_web.file.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class File extends Domain {

  private UUID id;
  private String originalName;     // 업로드 시 사용자가 지정한 파일 이름
  private String savedName;        // 서버에 저장된 이름 (UUID 등으로 변경 가능)
  private String path;             // 저장된 경로
  private long size;               // 파일 크기
  private ContentType contentType;      // MIME 타입
  private FileUseType useType;     // 사용처 (예: 프로필 이미지, 첨부파일 등)

  private UUID originalFileId;

  @Builder
  public File(LocalDateTime createdDate, LocalDateTime modifiedDate, String createdBy, String updatedBy,
      UUID id, String originalName, String savedName, String path,
      long size, ContentType contentType, FileUseType useType, UUID originalFileId) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.originalName = originalName;
    this.savedName = savedName;
    this.path = path;
    this.size = size;
    this.contentType = contentType;
    this.useType = useType;
    this.originalFileId = originalFileId;
  }

}
