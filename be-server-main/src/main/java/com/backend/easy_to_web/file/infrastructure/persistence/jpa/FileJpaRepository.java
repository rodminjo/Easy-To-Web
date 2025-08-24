package com.backend.easy_to_web.file.infrastructure.persistence.jpa;

import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.infrastructure.persistence.entity.FileEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileEntity, UUID> {

  Optional<FileEntity> findByOriginalFileIdAndContentType(UUID originalFileId, ContentType contentType);

  List<FileEntity> findAllByOriginalFileId(UUID originalFileId);

  void deleteAllByOriginalFileId(UUID originalFileId);
}
