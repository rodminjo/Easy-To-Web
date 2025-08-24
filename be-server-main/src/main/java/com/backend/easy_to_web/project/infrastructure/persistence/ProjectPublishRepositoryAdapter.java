package com.backend.easy_to_web.project.infrastructure.persistence;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.project.domain.model.Publish;
import com.backend.easy_to_web.project.domain.port.out.ProjectPublishRepository;
import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectPublishEntity;
import com.backend.easy_to_web.project.infrastructure.persistence.jpa.ProjectPublishJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProjectPublishRepositoryAdapter implements ProjectPublishRepository {

	private final ProjectPublishJpaRepository projectPublishJpaRepository;

	@Override
	public Publish getByUrl(String url) {
		return projectPublishJpaRepository.findByUrl(url)
			.orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.PUBLISH_NOT_FOUND))
			.toDomain();
	}

	@Override
	public Optional<Publish> findByProjectId(UUID projectId) {
		return projectPublishJpaRepository.findByProjectId(projectId)
				.map(ProjectPublishEntity::toDomain);
	}


	@Override
	public Publish getByProjectId(UUID projectId) {
		return findByProjectId(projectId)
				.orElseThrow(() -> new CustomIllegalArgumentException(ExceptionMessage.PROJECT_PUBLISH_NOT_FOUND));
	}

	@Override
	public Publish save(Publish publish) {
		ProjectPublishEntity entity = ProjectPublishEntity.of(publish);
		return projectPublishJpaRepository.save(entity).toDomain();
	}

	@Override
	public void deleteByProjectId(UUID projectId){
		projectPublishJpaRepository.deleteByProjectId(projectId);
	}
}
