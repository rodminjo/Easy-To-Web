package com.backend.easy_to_web.project.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateProjectUseCase implements UseCase {

  private final String title;
  private final String description;


  public Project createProject(){
    return Project.builder()
        .title(title)
        .description(description)
        .status(ProjectStatus.CLOSED)
        .members(new HashSet<>())
        .build();
  }
}
