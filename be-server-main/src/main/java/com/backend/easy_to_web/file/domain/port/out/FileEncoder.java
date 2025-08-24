package com.backend.easy_to_web.file.domain.port.out;

import com.backend.easy_to_web.file.domain.model.ContentType;
import com.backend.easy_to_web.file.domain.model.EncodeFile;
import com.backend.easy_to_web.file.domain.model.File;

public interface FileEncoder {

  EncodeFile encode(File file, ContentType encodeType);

  boolean supports(File file, ContentType encodeType);

}
