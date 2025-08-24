package com.backend.easy_to_web.file.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EncodeFile {

  private UUID id;               // id
  private String originalName;   // 원본 이름 (예: abc.jpg)
  private String savedName;      // 저장된 이름 (예: uuid)
  private String path;           // 인코딩 후 저장 경로
  private long size;             // 전체 크기
  private ContentType contentType;    // MIME 타입

  private UUID originalFileId;


  public static EncodeFile fromFile(File file, UUID newId,String encodedPath, long encodedSize ,ContentType encodeType){
    return EncodeFile.builder()
        .id(newId)
        .originalName(changeExtension(file.getOriginalName(), encodeType.getExtension()))
        .savedName(newId.toString())
        .path(encodedPath)
        .size(encodedSize)
        .contentType(encodeType)
        .originalFileId(file.getId())
        .build();
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
        .originalFileId(this.originalFileId)
        .build();

  }

  private static String changeExtension(String fileName, String newExtension) {
    int lastDot = fileName.lastIndexOf('.');
    if (lastDot == -1) return fileName + "." + newExtension;
    return fileName.substring(0, lastDot + 1) + newExtension;
  }
}