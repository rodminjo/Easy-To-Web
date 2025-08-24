package com.backend.easy_to_web.file.infrastructure.external;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.FileProperties;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.port.out.FileStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocalFileStorage implements FileStorage {

  private final Path storageDir;

  public LocalFileStorage(FileProperties properties) {
    this.storageDir = Paths.get(properties.getStorageDir());
    try {
      Files.createDirectories(storageDir);
    } catch (IOException e) {
      log.error("Failed to create storage directory", e);
      throw new CustomIllegalStateException(ExceptionMessage.CREATE_DIRECTORY_FAILED);
    }
  }

  @Override
  public String store(UUID newId, String filePath) {
    try {
      Path source = Paths.get(filePath);
      Path target = storageDir.resolve(newId.toString());

      // 파일 복사 (기존 파일 덮어쓰기)
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

      // 경로 반환
      return target.toString();

    } catch (IOException e) {
      log.error("Failed to store merged file", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_SAVE_FAILED);
    }
  }

  @Override
  public InputStream load(File file) {
    try {
      Path path = storageDir.resolve(file.getSavedName());
      if (!Files.exists(path)) {
        log.info("File not found: {}", file.getSavedName());
        throw new CustomIllegalArgumentException(ExceptionMessage.FILE_NOT_FOUND);
      }

      return Files.newInputStream(path, StandardOpenOption.READ);

    } catch (IOException e) {
      log.error("Failed to load file input stream", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_LOAD_FAILED);
    }
  }


  @Override
  public void delete(UUID id) {
    try {
      Path file = storageDir.resolve(id.toString());
      Files.deleteIfExists(file);
    } catch (IOException e) {
      log.error("Failed to delete stored file", e);
      throw new CustomIllegalStateException(ExceptionMessage.FILE_DELETE_FAILED);
    }
  }


}
