package com.backend.easy_to_web.project.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;


@Getter
@AllArgsConstructor
public class GetProjectHistoryListUseCase implements UseCase {
  private final UUID projectId;
  private final Pageable pageable;
}
