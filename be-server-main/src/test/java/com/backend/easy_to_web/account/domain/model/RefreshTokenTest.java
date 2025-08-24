package com.backend.easy_to_web.account.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.easy_to_web.test_support.model.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class RefreshTokenTest extends UnitTest {

  @Test
  @DisplayName("토큰 업데이트 테스트")
  void shouldUpdateTokenWhenUpdateMethodIsCalled() {
    // given
    long id = 1L;
    String initialToken = "initial-token";
    String userIp = "user-ip";
    String userAgent = "user-agent";
    RefreshToken refreshToken = RefreshToken.builder()
        .id(id)
        .token(initialToken)
        .userIp(userIp)
        .userAgent(userAgent)
        .build();

    String newToken = "updated-token";

    // when
    refreshToken.update(newToken, userIp, userAgent);

    // then
    assertEquals(newToken, refreshToken.getToken(), "Token이 업데이트되어야 합니다.");
  }
}