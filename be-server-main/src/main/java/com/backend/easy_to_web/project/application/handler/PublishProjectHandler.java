package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.project.domain.model.ProjectStatus;
import com.backend.easy_to_web.project.domain.result.PublishProjectResult;
import com.backend.easy_to_web.project.domain.usecase.PublishProjectUseCase;
import com.backend.easy_to_web.project.domain.model.History;
import com.backend.easy_to_web.project.domain.model.Project;
import com.backend.easy_to_web.project.domain.model.ProjectPermission;
import com.backend.easy_to_web.project.domain.model.Publish;
import com.backend.easy_to_web.project.domain.port.out.ProjectHistoryRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectPublishRepository;
import com.backend.easy_to_web.project.domain.port.out.ProjectRepository;
import com.backend.easy_to_web.project.domain.port.out.SlugGenerator;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Handler
public class PublishProjectHandler implements UseCaseHandler<PublishProjectResult, PublishProjectUseCase> {

	private final ProjectPublishRepository projectPublishRepository;
	private final ProjectRepository projectRepository;
	private final SlugGenerator slugGenerator;


	@Override
	public Class<PublishProjectUseCase> getUseCaseClass() {
		return PublishProjectUseCase.class;
	}

	@Override
	public PublishProjectResult handle(PublishProjectUseCase useCase) {
		UUID accountId = SecurityScopeUtils.getAccountId();

		Project project = projectRepository.getByProjectId(useCase.getProjectId());
		project.ensureHasPermission(accountId, ProjectPermission.OWNER);

		Publish publish = projectPublishRepository.findByProjectId(project.getId())
				.map(existing -> {
					existing.updateContent(useCase.getContent());
					return existing;
				})
				.orElseGet(() -> {
					String url = slugGenerator.generate(project.getTitle(), project.getId().toString());
					return Publish.builder()
							.projectId(project.getId())
							.url(url)
							.content(useCase.getContent())
							.viewCount(0)
							.build();
				});

		Publish saved = projectPublishRepository.save(publish);

		project.updateStatus(ProjectStatus.OPEN);
		projectRepository.save(project);

		return new PublishProjectResult(saved.getUrl());
	}
}
