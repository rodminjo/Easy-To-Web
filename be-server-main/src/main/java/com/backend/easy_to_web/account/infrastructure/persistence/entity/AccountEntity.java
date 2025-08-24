package com.backend.easy_to_web.account.infrastructure.persistence.entity;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("계정 테이블")
@Entity
@Table(
    name = "account"
)
public class AccountEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Comment("계정 고유 ID") // 컬럼 주석
  private UUID id;

  @Column(unique = true, nullable = false)
  @Comment("사용자 이메일 (로그인 ID)")
  private String email;

  @Column(nullable = false, length = 1000)
  @Comment("암호화된 비밀번호")
  private String password;

  @Column
  @Comment("사용자 닉네임")
  private String nickname;

  @Column
  @Comment("프로필 이미지 URL")
  private String profileUrl;


  public Account toDomain() {
    return Account.builder()
        .id(id)
        .email(email)
        .password(password)
        .nickname(nickname)
        .profileUrl(profileUrl)
        .roles(new HashSet<>())
        .createdDate(getCreatedDate())
        .createdBy(getCreatedBy())
        .modifiedDate(getModifiedDate())
        .updatedBy(getUpdatedBy())
        .build();
  }

  public static AccountEntity of(Account domain) {
    return new AccountEntity(domain.getId(), domain.getEmail(), domain.getPassword(), domain.getNickname(), domain.getProfileUrl());
  }
}
