package com.backend.easy_to_web.account.infrastructure.persistence.entity;

import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("계정-역할 테이블")
@Entity
@Table(name = "account_role")
public class AccountRoleEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  @Comment("PK: 자동 증가 ID")
  private long id;

  @Column(nullable = false)
  @Comment("Account FK: 계정 UUID")
  private UUID accountId;

  @Column(nullable = false)
  @Comment("Role ID (예: ROLE_USER, ROLE_ADMIN 등)")
  private String roleId;


}
