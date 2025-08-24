package com.backend.easy_to_web.file.infrastructure.external;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.FileProperties;
import com.backend.easy_to_web.file.domain.model.ChunkFile;
import com.backend.easy_to_web.file.domain.model.MergeFile;
import com.backend.easy_to_web.file.domain.port.out.ChunkUploader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocalChunkUploader implements ChunkUploader {


  private final Path chunkBaseDir;
  private final String mergedSuffix;


  public LocalChunkUploader(FileProperties properties) {
    this.chunkBaseDir = Paths.get(properties.getChunkDir());
    this.mergedSuffix = properties.getMergedSuffix() != null ? properties.getMergedSuffix() : "_merged";

    try {
      Files.createDirectories(chunkBaseDir);
    } catch (IOException e) {
      log.error("Failed to create chunk upload directory", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public void saveChunk(ChunkFile file) {
    try {
      Path chunkDir = chunkBaseDir.resolve(file.getId().toString());
      Files.createDirectories(chunkDir);

      // 1. 청크 저장
      Path chunkFile = chunkDir.resolve(String.valueOf(file.getChunkNumber()));
      file.getFile().transferTo(chunkFile.toFile());


    } catch (IOException e) {
      log.error("Failed to save chunk", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public boolean isUploadComplete(UUID chunkId, int totalChunks) {
    Path chunkDir = chunkBaseDir.resolve(chunkId.toString());

    try (Stream<Path> files = Files.list(chunkDir)) {
      return files.count() == totalChunks;

    } catch (IOException e) {
      log.error("Failed to check upload completion", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public MergeFile mergeChunks(UUID chunkId, String originalName) {
    Path chunkDir = chunkBaseDir.resolve(chunkId.toString());

    try {
      // 청크 파일 정렬
      List<Path> sortedChunks = Files.list(chunkDir)
          .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getFileName().toString())))
          .toList();

      // 병합 대상 경로 (확장자 붙여서 저장)
      String mergedFileName = chunkId + mergedSuffix;
      Path mergedFile = chunkBaseDir.resolve(mergedFileName);

      // 병합 수행
      try (OutputStream out = new FileOutputStream(mergedFile.toFile())) {
        for (Path chunk : sortedChunks) {
          Files.copy(chunk, out);
        }
      }

      // 병합된 파일에서 메타데이터 추출
      long fileSize = Files.size(mergedFile);

      // MergeFile 객체 생성
      return MergeFile.builder()
          .id(chunkId)
          .savedName(chunkId.toString())
          .path(mergedFile.toString())
          .size(fileSize)
          .originalName(originalName)
          .build();

    } catch (IOException e) {
      log.error("Failed to merge chunks", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public void deleteChunksAndMerge(UUID chunkId) {
    Path chunkDir = chunkBaseDir.resolve(chunkId.toString());
    Path mergedFile = chunkBaseDir.resolve(chunkId + mergedSuffix);

    try (Stream<Path> files = Files.list(chunkDir)) {
      files.forEach(path -> path.toFile().delete());
      Files.deleteIfExists(chunkDir);
    } catch (IOException e) {
      log.error("Failed to delete chunk files", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_UPLOAD_FAILED);
    }

    try {
      Files.deleteIfExists(mergedFile);
    } catch (IOException e) {
      log.error("Failed to delete merged file", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_UPLOAD_FAILED);
    }
  }

}