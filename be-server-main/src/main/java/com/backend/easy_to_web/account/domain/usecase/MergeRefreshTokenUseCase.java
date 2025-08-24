package com.backend.easy_to_web.account.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class MergeRefreshTokenUseCase implements UseCase {

  private String accountId;
  private String refreshToken;
  private String userIp;
  private String userAgent;

}
