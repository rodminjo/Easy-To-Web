package com.backend.easy_to_web.account.infrastructure.controller.rest;

import com.backend.easy_to_web.common.infrastructure.controller.rest.BaseOutput;
import io.swagger.v3.oas.annotations.media.Schema;

public class GetProfileRest {

  @Schema(description = "계정정보 조회 output")
  public record GetProfileOutput(
      @Schema(description = "계정 id", example = "3578e885-f55b-4476-925c-5838b58f4607")
      String id,
      @Schema(description = "계정 이메일", example = "example@example.com")
      String email,
      @Schema(description = "계정 닉네임", example = "닉네임")
      String nickname,
      @Schema(description = "계정 프로필 이미지", example = "프로필 이미지 URL")
      String profileUrl
  ) implements BaseOutput {

  }
}
