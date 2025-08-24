package com.backend.easy_to_web.project.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class AcceptProjectInvitationUseCase implements UseCase {

  private final UUID projectId;
  private final String code;

}
