package com.backend.easy_to_web.file.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;


@Getter
public class ChunkFile {
  private UUID id;
  private int chunkNumber;
  private int totalChunks;
  private String fileName;
  private String contentType;
  private long fileSize;
  private MultipartFile file;

  @Builder
  public ChunkFile(UUID id, int chunkNumber, int totalChunks, String fileName, String contentType,
      long fileSize, MultipartFile file) {
    this.id = id;
    this.chunkNumber = chunkNumber;
    this.totalChunks = totalChunks;
    this.fileName = fileName;
    this.contentType = contentType;
    this.fileSize = fileSize;
    this.file = file;
  }

  public boolean isLastChunk(){
    return this.chunkNumber == totalChunks - 1;
  }
}
