package com.backend.easy_to_web.file.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class MergeFile {

  private UUID id;               // chunkId (병합 대상 식별자)
  private String originalName;   // 원본 이름 (예: abc.jpg)
  private String savedName;      // 저장된 이름 (예: abc_uuid.jpg 또는 uuid)
  private String path;           // 병합 후 저장 경로
  private long size;             // 전체 크기
  private ContentType contentType;    // MIME 타입

  @Builder
  public MergeFile(UUID id, String savedName, String path, long size, String originalName) {
    this.id = id;
    this.savedName = savedName;
    this.path = path;
    this.size = size;
    this.originalName = originalName;
    this.contentType = getContentTypeByExtension(getExtension(originalName));
  }

  public File toFile(UUID newId, String targetPath) {
    return File.builder()
        .id(newId)
        .originalName(this.originalName)
        .savedName(newId.toString())
        .path(targetPath)
        .size(this.size)
        .contentType(this.contentType)
        .useType(FileUseType.PROJECT_ATTACHMENT)
        .build();

  }

  // 확장자에 맞는 ContentType을 Enum으로 반환
  private ContentType getContentTypeByExtension(String extension) {
    return ContentType.fromExtension(extension);
  }

  private String getExtension(String fileName) {
    if (StringUtils.hasText(fileName)) {
      int dotIndex = fileName.lastIndexOf('.');
      if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
        return fileName.substring(dotIndex + 1).toLowerCase();
      }
    }
    return "";
  }
}