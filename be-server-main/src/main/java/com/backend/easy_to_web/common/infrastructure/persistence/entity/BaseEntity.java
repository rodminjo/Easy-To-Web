package com.backend.easy_to_web.common.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  @CreatedBy
  @Column(updatable = false, length = 500)
  private String createdBy;

  @LastModifiedBy
  @Column(length = 500)
  private String updatedBy;

  public BaseEntity() {
  }

  public BaseEntity(LocalDateTime createdDate, LocalDateTime modifiedDate, String createdBy, String updatedBy) {
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
  }
}
