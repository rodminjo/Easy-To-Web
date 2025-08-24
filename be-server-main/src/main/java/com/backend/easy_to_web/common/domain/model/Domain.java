package com.backend.easy_to_web.common.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Domain implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private String createdBy;
  private String updatedBy;


  public Domain(LocalDateTime createdDate, LocalDateTime modifiedDate, String createdBy,
      String updatedBy) {
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
  }
}
