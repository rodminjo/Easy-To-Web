package com.backend.easy_to_web.account.application.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.backend.easy_to_web.account.domain.model.RefreshToken;
import com.backend.easy_to_web.account.domain.port.out.RefreshTokenRepository;
import com.backend.easy_to_web.account.domain.usecase.MergeRefreshTokenUseCase;
import com.backend.easy_to_web.test_support.mock.account.handler.infrastructure.FakeRefreshTokenRepository;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class MergeRefreshTokenHandlerTest extends UnitTest {

  private final String accountId = "3578e885-f55b-4476-925c-5838b58f4607";
  private final String userIp = "testIp";
  private final String userAgent = "testAgent";

  private RefreshTokenRepository refreshTokenRepository;
  private MergeRefreshTokenHandler handler;

  // 공통으로 사용할 메서드
  private MergeRefreshTokenUseCase createCommand(String refreshToken) {
    return new MergeRefreshTokenUseCase(accountId, refreshToken, userIp, userAgent);
  }

  @BeforeEach
  void setUp() {
    refreshTokenRepository = new FakeRefreshTokenRepository();
    handler = new MergeRefreshTokenHandler(refreshTokenRepository);
  }

  @Test
  @DisplayName("이메일로 발급받은 토큰이 없다면 신규로 생성하여 저장하고 반환한다")
  void shouldSaveNewTokenWhenEmailDoesNotExist() {
    // given
    String refreshToken = "test-refresh-token";
    MergeRefreshTokenUseCase command = createCommand(refreshToken);

    // when
    handler.handleVoid(command);

    // then
    RefreshToken findToken = refreshTokenRepository.getAllByAccountId(accountId).stream().findFirst().get();
    assertEquals(refreshToken, findToken.getToken());
    assertEquals(accountId, findToken.getAccountId());
  }

  @Test
  @DisplayName("이메일로 발급받은 토큰이 없고 기존에 같은 이메일로 발급받은 토큰이 3개 이상이면 삭제한 후 저장하고 반환한다")
  void shouldDeleteAndSaveNewTokenWhenEmailExistsAndThreeOrMoreTokensExist() {
    // given
    for (int i = 0; i < 3; i++) {
      refreshTokenRepository.save(
          RefreshToken.builder()
              .id(0)
              .token("test-refresh-token" + i)
              .accountId(accountId)
              .userIp(userIp)
              .userAgent(userAgent)
              .createdDate(LocalDateTime.now())
              .createdBy(userIp + userAgent)
              .modifiedDate(LocalDateTime.now())
              .updatedBy(userIp + userAgent)
              .build()
      );
    }

    String refreshToken = "test-refresh-token";
    MergeRefreshTokenUseCase command = createCommand(refreshToken);

    // when
    handler.handleVoid(command);

    // then
    List<RefreshToken> list = refreshTokenRepository.getAllByAccountId(accountId);
    Optional<RefreshToken> findToken = list.stream()
        .filter(value -> value.getToken().equals(refreshToken))
        .findFirst();
    Optional<RefreshToken> ifDeleteToken = list.stream()
        .filter(value -> value.getToken().equals(refreshToken + 0))
        .findFirst();

    assertEquals(3, list.size());
    assertTrue(findToken.isPresent());
    assertEquals(refreshToken, findToken.get().getToken());
    assertEquals(accountId, findToken.get().getAccountId());
    assertTrue(ifDeleteToken.isEmpty());
  }

  @Test
  @DisplayName("이메일로 발급받은 토큰이 있다면 신규 토큰으로 수정하고 반환한다")
  void shouldUpdateNewTokenWhenEmailExists() {
    // given
    long id = 1L;
    String oldRefreshToken = "test-refresh-token";
    String newRefreshToken = "new-refresh-token";
    RefreshToken token = RefreshToken.builder()
        .id(id)
        .accountId(accountId)
        .token(oldRefreshToken)
        .userIp(userIp)
        .userAgent(userAgent)
        .build();
    refreshTokenRepository.save(token);

    MergeRefreshTokenUseCase command = new MergeRefreshTokenUseCase(accountId, newRefreshToken, userIp,
        userAgent);

    // when
    handler.handleVoid(command);

    // then
    assertEquals(newRefreshToken,
        refreshTokenRepository.getAllByAccountId(accountId).stream().findFirst().get().getToken());
  }
}