package com.backend.easy_to_web.project.domain.model;

import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member extends BaseEntity {

  private long id;
  private UUID projectId;
  private UUID accountId; // account 도메인의 id
  private ProjectPermission permission;



  @Builder
  public Member(LocalDateTime createdDate, LocalDateTime modifiedDate,
      String createdBy, String updatedBy, long id, UUID projectId, UUID accountId, ProjectPermission permission) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.projectId = projectId;
    this.accountId = accountId;
    this.permission = permission;
  }


  public static Member create(UUID projectId, UUID accountId, ProjectPermission permission){
    return Member.builder()
       .projectId(projectId)
       .accountId(accountId)
       .permission(permission)
       .build();
  }

  public void updatePermission(ProjectPermission changerPermission, ProjectPermission newPermission){
    ensureNotOwner();

    // 자신보다 아래 등급만 가능
    if (!changerPermission.isValidPermission(newPermission)) {
      throw new CustomIllegalArgumentException(ExceptionMessage.PERMISSION_TOO_LOW_TO_UPDATE);
    }

    this.permission = newPermission;
  }

  public void ensureDeletePermissionIsValid(ProjectPermission changerPermission){
    ensureNotOwner();

    // 자신보다 아래 등급만 가능
    if (!changerPermission.isValidPermission(this.permission)) {
      throw new CustomIllegalArgumentException(ExceptionMessage.PERMISSION_TOO_LOW_TO_UPDATE);
    }
  }

  public void ensureNotOwner() {
    boolean originalPermissionIsOwner = this.permission.equals(ProjectPermission.OWNER);
    if (originalPermissionIsOwner){
      throw new CustomIllegalArgumentException(ExceptionMessage.PROJECT_OWNER_NOT_UPDATABLE);
    }
  }
}
