package com.backend.easy_to_web.account.domain.result;

import com.backend.easy_to_web.account.domain.model.Account;
import com.backend.easy_to_web.common.application.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GetProfileResult implements Result {

  private String id;
  private String email;
  private String nickname;
  private String profileUrl;

  public static GetProfileResult of(Account account){
    return new GetProfileResult(
        account.getId().toString(),
        account.getEmail(),
        account.getNickname(),
        account.getProfileUrl()
    );

  }
}
