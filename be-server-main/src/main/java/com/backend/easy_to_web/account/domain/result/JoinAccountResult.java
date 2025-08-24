package com.backend.easy_to_web.account.domain.result;

import com.backend.easy_to_web.common.application.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinAccountResult implements Result {
  String email;
}
