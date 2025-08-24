package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.project.domain.result.UpdateProjectThumbnailResult;
import com.backend.easy_to_web.project.domain.usecase.UpdateProjectThumbnailUseCase;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class UpdateProjectThumbnailHandler implements UseCaseHandler<UpdateProjectThumbnailResult, UpdateProjectThumbnailUseCase> {

  private final ProjectRepository projectRepository;


  @Override
  public Class<UpdateProjectThumbnailUseCase> getUseCaseClass() {
    return UpdateProjectThumbnailUseCase.class;

  }

  @Override
  @Transactional
  public UpdateProjectThumbnailResult handle(UpdateProjectThumbnailUseCase useCase) {
    // 프로젝트 권한 체크
    UUID accountId = SecurityScopeUtils.getAccountId();
    Project project = projectRepository.getByProjectId(useCase.getId());
    project.ensureHasPermission(accountId, ProjectPermission.READ_ONLY);

    // 과거 Thumbnail
    UUID newThumbnail = useCase.getThumbnailFileId();
    UUID pastThumbnail = project.getThumbnailFileId();

    // 신규 저장 및 과거 id return
    project.updateThumbnailFileId(newThumbnail);
    projectRepository.save(project);

    return new UpdateProjectThumbnailResult(newThumbnail, pastThumbnail);
  }
}
