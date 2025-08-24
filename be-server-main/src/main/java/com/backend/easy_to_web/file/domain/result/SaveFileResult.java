package com.backend.easy_to_web.file.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class SaveFileResult implements Result {

  boolean isComplete;
  int uploadedChunks;
  int totalChunks;
  UUID fileId;

}
