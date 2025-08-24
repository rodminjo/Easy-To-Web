package com.backend.easy_to_web.file.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;


@Getter
@AllArgsConstructor
public class LoadFileResult implements Result {

  Resource file;
  String fileName;

}
