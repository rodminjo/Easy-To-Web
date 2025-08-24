package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import com.backend.easy_to_web.project.domain.usecase.PublishProjectUseCase;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.port.out.ProjectPublishRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.project.domain.usecase.UnpublishProjectUseCase;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Handler
public class UnpublishProjectHandler implements VoidUseCaseHandler<UnpublishProjectUseCase> {

	private final ProjectRepository projectRepository;
	private final ProjectPublishRepository projectPublishRepository;


	@Override
	public Class<UnpublishProjectUseCase> getVoidUseCaseClass() {
		return UnpublishProjectUseCase.class;
	}

	@Override
	@Transactional
	public void handleVoid(UnpublishProjectUseCase useCase) {
		UUID accountId = SecurityScopeUtils.getAccountId();
		Project project = projectRepository.getByProjectId(useCase.getProjectId());
		project.ensureHasPermission(accountId, ProjectPermission.OWNER);

		project.updateStatus(ProjectStatus.CLOSED);
		projectRepository.save(project);

		projectPublishRepository.deleteByProjectId(project.getId());
	}
}
