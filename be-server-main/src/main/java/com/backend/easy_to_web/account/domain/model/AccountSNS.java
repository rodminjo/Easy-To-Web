package com.backend.easy_to_web.account.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountSNS extends Domain {

  private Long id;
  private UUID accountId;
  private String email;
  private String providerAccountId;
  private SNSProvider provider;


  @Builder
  public AccountSNS(LocalDateTime createdDate, LocalDateTime modifiedDate,
      String createdBy, String updatedBy, Long id, UUID accountId, String email, String providerAccountId,
      SNSProvider provider) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.accountId = accountId;
    this.email = email;
    this.providerAccountId = providerAccountId;
    this.provider = provider;
  }
}
