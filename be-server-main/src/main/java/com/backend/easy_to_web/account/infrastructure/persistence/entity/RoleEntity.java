package com.backend.easy_to_web.account.infrastructure.persistence.entity;

import com.backend.easy_to_web.account.domain.model.Role;
import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("권한 테이블")
@Entity
@Table(name = "role")
public class RoleEntity extends BaseEntity {
  @Id
  @Column(nullable = false, unique = true, length = 50)
  @Comment("권한 ID (예: ROLE_ADMIN, ROLE_USER)")
  private String id;

  @Column
  @Comment("권한 이름 (예: 관리자, 일반 사용자 등)")
  private String roleName;

  @Column
  @Comment("권한에 대한 설명")
  private String description;


  public Role toDomain() {
    return Role.builder()
        .id(this.id)
        .roleName(this.roleName)
        .description(this.description)
        .createdDate(getCreatedDate())
        .createdBy(getCreatedBy())
        .modifiedDate(getModifiedDate())
        .updatedBy(getUpdatedBy())
        .build();

  }

  public static RoleEntity of(Role domain) {
    return new RoleEntity(domain.getId(), domain.getRoleName(), domain.getDescription());
  }
}
