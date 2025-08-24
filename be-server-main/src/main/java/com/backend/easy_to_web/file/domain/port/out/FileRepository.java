package com.backend.easy_to_web.file.domain.port.out;

import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.domain.model.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository {

  Optional<File> findById(UUID id);

  File getById(UUID id);

  File save(File file);

  List<File> findAllByOriginalFileId(UUID id);

  // 특정 포맷의 인코딩 파일만 조회
  Optional<File> findByOriginalFileIdAndContentType(UUID originalFileId, ContentType contentType);

  void delete(File file);

  void deleteAllByOriginalFileId(UUID id);

}
