package com.backend.easy_to_web.project.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessProjectResult implements Result {

  private final ProjectPermission permission;

}
