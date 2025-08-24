package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.model.Publish;
import com.backend.easy_to_web.project.domain.port.out.ProjectPublishRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.project.domain.usecase.RepublishProjectUseCase;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Handler
public class RepublishProjectHandler implements VoidUseCaseHandler<RepublishProjectUseCase> {

	private final ProjectRepository projectRepository;
	private final ProjectPublishRepository projectPublishRepository;


	@Override
	public Class<RepublishProjectUseCase> getVoidUseCaseClass() {
		return RepublishProjectUseCase.class;
	}

	@Override
	@Transactional
	public void handleVoid(RepublishProjectUseCase useCase) {
		UUID accountId = SecurityScopeUtils.getAccountId();
		Project project = projectRepository.getByProjectId(useCase.getProjectId());
		project.ensureHasPermission(accountId, ProjectPermission.OWNER);

		Publish publish = projectPublishRepository.getByProjectId(project.getId());
		publish.updateContent(useCase.getContent());
		projectPublishRepository.save(publish);
	}
}
