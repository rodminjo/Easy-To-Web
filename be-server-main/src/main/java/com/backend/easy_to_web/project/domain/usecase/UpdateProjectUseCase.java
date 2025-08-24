package com.backend.easy_to_web.project.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UpdateProjectUseCase implements UseCase {

  private final UUID id;
  private final String title;
  private final String description;

}
