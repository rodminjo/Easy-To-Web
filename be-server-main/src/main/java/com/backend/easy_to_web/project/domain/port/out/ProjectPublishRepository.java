package com.backend.easy_to_web.project.domain.port.out;


import com.backend.easy_to_web.project.domain.model.Publish;
import java.util.Optional;
import java.util.UUID;

public interface ProjectPublishRepository {

  Publish getByUrl(String url);

  Optional<Publish> findByProjectId(UUID projectId);

  Publish getByProjectId(UUID projectId);

  Publish save(Publish publish);

  void deleteByProjectId(UUID projectId);

}
