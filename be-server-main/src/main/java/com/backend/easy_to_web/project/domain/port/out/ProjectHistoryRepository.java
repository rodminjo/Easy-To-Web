package com.backend.easy_to_web.project.domain.port.out;

import com.backend.easy_to_web.project.domain.model.History;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectHistoryRepository {

  Optional<History> findRecentOne(UUID projectId);

  History getRecentOne(UUID projectId);

  History save(History history);

  Page<History> findAll(UUID projectId, Pageable pageable);

  Optional<History> findByProjectIdAndId(UUID projectId, long id);

}
