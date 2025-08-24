package com.backend.easy_to_web.file.domain.port.out;

import com.backend.easy_to_web.file.domain.model.File;
import java.io.InputStream;
import java.util.UUID;

public interface FileStorage {

  String store(UUID newId, String filePath);

  InputStream load(File file);

  void delete(UUID id);

}
