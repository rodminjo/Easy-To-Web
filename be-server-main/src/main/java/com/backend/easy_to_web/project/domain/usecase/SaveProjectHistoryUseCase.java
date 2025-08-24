package com.backend.easy_to_web.project.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import com.backend.easy_to_web.project.domain.model.History;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveProjectHistoryUseCase implements UseCase {

  String projectId;
  byte[] snapshot;
  Set<UUID> editors;


  public History createHistory(){
    return History.builder()
        .id(0)
        .projectId(UUID.fromString(projectId))
        .content(snapshot)
        .editor(new HashSet<>(editors))
        .editTime(LocalDateTime.now())
        .build();
  }


}
