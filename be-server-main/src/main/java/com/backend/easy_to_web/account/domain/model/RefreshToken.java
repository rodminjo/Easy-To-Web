package com.backend.easy_to_web.account.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import com.backend.easy_to_web.common.domain.exception.CustomIllegalArgumentException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshToken extends Domain {
  private long id;
  private String accountId;
  private String token;
  private String userIp;
  private String userAgent;

  @Builder
  public RefreshToken(LocalDateTime createdDate, LocalDateTime modifiedDate,
      String createdBy, String updatedBy, long id, String accountId, String token, String userIp,
      String userAgent) {
    super(createdDate, modifiedDate, createdBy, updatedBy);
    this.id = id;
    this.accountId = accountId;
    this.token = token;
    this.userIp = userIp;
    this.userAgent = userAgent;
  }

  public void update(String newToken, String userIp, String userAgent) {
    if (!validate(userAgent)){
      throw new CustomIllegalArgumentException(ExceptionMessage.USER_AGENT_INVALID);
    }

    this.token = newToken;
    this.userIp = userIp;
  }

  private boolean validate(String userAgent){
    return userAgent.equals(this.userAgent);
  }
}
