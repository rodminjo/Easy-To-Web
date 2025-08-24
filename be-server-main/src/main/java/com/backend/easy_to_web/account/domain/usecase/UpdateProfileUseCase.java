package com.backend.easy_to_web.account.domain.usecase;

import com.backend.easy_to_web.common.application.usecase.UseCase;
import lombok.Getter;

@Getter
public class UpdateProfileUseCase implements UseCase {

  private final String nickname;
  private final String profileUrl;

  public UpdateProfileUseCase(String nickname, String profileUrl) {
    this.nickname = nickname;
    this.profileUrl = profileUrl;
  }
}
