package com.backend.easy_to_web.account.infrastructure.persistence.entity;

import com.backend.easy_to_web.account.domain.model.AccountSNS;
import com.backend.easy_to_web.account.domain.model.SNSProvider;
import com.backend.easy_to_web.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Comment("계정-소셜 로그인 테이블")
@Entity
@Table(name = "account_sns")
public class AccountSNSEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Comment("계정 고유 ID")
  @Column(nullable = false)
  private UUID accountId;

  @Comment("소셜 로그인 이메일")
  @Column(nullable = false)
  private String email;

  @Comment("소셜 로그인에서 제공받은 고유 id")
  @Column(nullable = false)
  private String providerAccountId;

  @Comment("소셜 로그인 제공자")
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SNSProvider provider;


  public AccountSNS toDomain() {
    return AccountSNS.builder()
        .id(id)
        .accountId(accountId)
        .email(email)
        .provider(provider)
        .providerAccountId(providerAccountId)
        .build();

  }

  public static AccountSNSEntity of(AccountSNS accountSNS){
    AccountSNSEntity entity = new AccountSNSEntity();
    entity.id = accountSNS.getId();
    entity.accountId = accountSNS.getAccountId();
    entity.email = accountSNS.getEmail();
    entity.provider = accountSNS.getProvider();
    entity.providerAccountId = accountSNS.getProviderAccountId();

    return entity;

  }

}