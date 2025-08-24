package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetProjectHistoryResult implements Result {

  private final byte[] content;
  private final LocalDateTime editTime;
  private final List<String> editor;



}
