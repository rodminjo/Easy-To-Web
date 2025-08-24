package com.backend.easy_to_web.file.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.file.domain.model.ChunkFile;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@Getter
public class SaveFileUseCase implements UseCase {

  private final UUID id;
  private final int chunkNumber;
  private final int totalChunks;
  private final String fileName;
  private final String contentType;
  private final long fileSize;
  private final MultipartFile file;


  public ChunkFile toChunkFile(){
    return ChunkFile.builder()
        .id(this.id)
        .chunkNumber(this.chunkNumber)
        .totalChunks(this.totalChunks)
        .fileName(this.fileName)
        .contentType(this.contentType)
        .fileSize(this.fileSize)
        .file(this.file)
        .build();
  }

}
