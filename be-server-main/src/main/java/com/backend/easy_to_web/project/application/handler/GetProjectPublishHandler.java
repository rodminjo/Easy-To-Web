package com.backend.easy_to_web.project.application.handler;

import com.backend.easy_to_web.common.application.handler.Handler;
import com.backend.easy_to_web.common.application.handler.UseCaseHandler;
import com.backend.easy_to_web.project.domain.port.out.ProjectPublishRepository;
import com.backend.easy_to_web.project.domain.result.GetProjectPublishResult;
import com.backend.easy_to_web.project.domain.usecase.GetProjectPublishUseCase;
import com.backend.easy_to_web.project.domain.model.Publish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Handler
public class GetProjectPublishHandler implements UseCaseHandler<GetProjectPublishResult, GetProjectPublishUseCase> {

	private final ProjectPublishRepository projectPublishRepository;


	@Override
	public Class<GetProjectPublishUseCase> getUseCaseClass() {
		return GetProjectPublishUseCase.class;
	}

	@Override
	@Transactional(readOnly = true)
	public GetProjectPublishResult handle(GetProjectPublishUseCase useCase) {
		Publish publish = projectPublishRepository.getByUrl(useCase.getUrl());
		return new GetProjectPublishResult(publish.getContent());
	}
}
