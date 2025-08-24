package com.backend.easy_to_web.project.infrastructure.persistence.jpa;

import com.backend.easy_to_web.project.infrastructure.persistence.entity.ProjectEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {


}
