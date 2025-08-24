package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateProjectThumbnailResult implements Result {

  private final UUID newProjectThumbnailFileId;
  private final UUID pastProjectThumbnailFileId;

}
