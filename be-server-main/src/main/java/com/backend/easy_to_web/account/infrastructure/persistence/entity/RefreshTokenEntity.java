package com.backend.easy_to_web.account.infrastructure.persistence.entity;

import com.backend.easy_to_web.account.domain.model.RefreshToken;
import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component("리프레시 토큰 테이블")
@Entity
@Table(name = "refresh_token")
public class RefreshTokenEntity extends BaseEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("PK: 자동 증가 ID")
  private long id;

  @Column(nullable = false, length = 1000)
  @Comment("계정 ID 또는 UUID (연결된 사용자)")
  private String accountId;

  @Column(nullable = false, length = 1000)
  @Comment("저장된 리프레시 토큰 문자열")
  private String token;

  @Column(nullable = false, length = 1000)
  @Comment("토큰 발급 시점의 사용자 IP")
  private String userIp;

  @Column(nullable = false, length = 1000)
  @Comment("토큰 발급 시점의 사용자 User-Agent")
  private String userAgent;


  @Builder
  public RefreshTokenEntity(LocalDateTime createdDate, LocalDateTime modifiedDate,
      String createdBy, String updatedBy, long id, String accountId, String token, String userIp,
      String userAgent) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.accountId = accountId;
    this.token = token;
    this.userIp = userIp;
    this.userAgent = userAgent;
  }


  public RefreshToken toDomain() {
    return RefreshToken.builder()
        .id(this.id)
        .accountId(this.accountId)
        .token(this.token)
        .userIp(this.userIp)
        .userAgent(this.userAgent)
        .createdDate(getCreatedDate())
        .createdBy(getCreatedBy())
        .modifiedDate(getModifiedDate())
        .updatedBy(getUpdatedBy())
        .build();
  }

  public static RefreshTokenEntity of(RefreshToken domain) {
    return RefreshTokenEntity.builder()
        .id(domain.getId())
        .accountId(domain.getAccountId())
        .token(domain.getToken())
        .userIp(domain.getUserIp())
        .userAgent(domain.getUserAgent())
        .createdBy(domain.getCreatedBy())
        .createdDate(domain.getCreatedDate())
        .updatedBy(domain.getUpdatedBy())
        .modifiedDate(domain.getModifiedDate())
        .build();

  }
}
