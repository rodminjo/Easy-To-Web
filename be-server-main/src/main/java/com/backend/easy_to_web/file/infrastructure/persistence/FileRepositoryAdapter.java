package com.backend.easy_to_web.file.infrastructure.persistence;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.domain.model.File;
import com.backend.easy_to_web.file.domain.port.out.FileRepository;
import com.backend.easy_to_web.file.infrastructure.persistence.entity.FileEntity;
import com.backend.easy_to_web.file.infrastructure.persistence.jpa.FileJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FileRepositoryAdapter implements FileRepository {

  private final FileJpaRepository fileJpaRepository;

  @Override
  public Optional<File> findById(UUID id) {
    return fileJpaRepository.findById(id)
        .map(FileEntity::toDomain);
  }

  @Override
  public File getById(UUID id) {
    return findById(id)
        .orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.FILE_NOT_FOUND));
  }

  @Override
  public File save(File file) {
    FileEntity fileEntity = FileEntity.of(file);
    return fileJpaRepository.save(fileEntity).toDomain();
  }

  @Override
  public List<File> findAllByOriginalFileId(UUID id) {
    List<FileEntity> findAll = fileJpaRepository.findAllByOriginalFileId(id);
    return findAll.stream()
        .map(FileEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<File> findByOriginalFileIdAndContentType(UUID originalFileId,
      ContentType contentType) {
    return fileJpaRepository.findByOriginalFileIdAndContentType(originalFileId, contentType)
        .map(FileEntity::toDomain);
  }

  @Override
  public void delete(File file) {
    FileEntity fileEntity = FileEntity.of(file);
    fileJpaRepository.delete(fileEntity);
  }

  @Override
  public void deleteAllByOriginalFileId(UUID id) {
    fileJpaRepository.deleteAllByOriginalFileId(id);
  }
}
