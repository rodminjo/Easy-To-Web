package com.backend.easy_to_web.account.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Role extends Domain {
  private String id;
  private String roleName;
  private String description;


  @Builder
  public Role(LocalDateTime createdDate, LocalDateTime modifiedDate, String createdBy,
      String updatedBy, String id, String roleName, String description) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.roleName = roleName;
    this.description = description;
  }
}
