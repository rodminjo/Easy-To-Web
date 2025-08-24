package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalStateException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.result.CreateProjectResult;
import com.backend.easy_to_web.project.domain.usecase.CreateProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.Member;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Handler
public class CreateProjectHandler implements UseCaseHandler<CreateProjectResult, CreateProjectUseCase> {

  private final ProjectRepository projectRepository;


  @Override
  public Class<CreateProjectUseCase> getUseCaseClass() {
    return CreateProjectUseCase.class;
  }

  @Override
  public CreateProjectResult handle(CreateProjectUseCase useCase) {
    String accountIdStr = (String) SecurityScopeUtils.getAuthentication().getPrincipal();
    if (accountIdStr == null) {
      throw new CustomIllegalStateException(ExceptionMessage.USER_NOT_LOGIN);
    }

    // 프로젝트 생성 및 accountId 확인
    UUID accountId = UUID.fromString(accountIdStr);
    Project project = useCase.createProject();

    // 멤버 입력 및 저장
    project.getMembers().add(Member.create(project.getId(), accountId, ProjectPermission.OWNER));
    Project saved = projectRepository.save(project);

    return new CreateProjectResult(saved.getId());
  }


}
