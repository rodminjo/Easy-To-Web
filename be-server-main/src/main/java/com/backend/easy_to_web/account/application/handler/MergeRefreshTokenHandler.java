package com.backend.easy_to_web.account.application.handler;

import com.backend.easy_to_web.account.domain.usecase.MergeRefreshTokenUseCase;
import com.backend.easy_to_web.account.domain.model.RefreshToken;
import com.backend.easy_to_web.account.domain.port.out.RefreshTokenRepository;
import com.backend.easy_to_web.common.application.handler.VoidUseCaseHandler;
import com.backend.easy_to_web.common.domain.model.Domain;
import com.backend.easy_to_web.common.application.handler.Handler;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Handler
public class MergeRefreshTokenHandler implements VoidUseCaseHandler<MergeRefreshTokenUseCase> {

  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public Class<MergeRefreshTokenUseCase> getVoidUseCaseClass() {
    return MergeRefreshTokenUseCase.class;
  }

  @Override
  @Transactional
  public void handleVoid(MergeRefreshTokenUseCase useCase) {
    List<RefreshToken> existingTokens = refreshTokenRepository.findAllByAccountId(useCase.getAccountId());

    RefreshToken matchedToken = existingTokens.stream()
        .filter(token ->
            token.getAccountId().equals(useCase.getAccountId())
                && token.getUserAgent().equals(useCase.getUserAgent())
        )
        .findFirst()
        .orElse(null);

    // 매칭되는 refresh Token이 없다면 가장 오래된 토큰 삭제, 신규 저장
    if (matchedToken == null) {
      if (existingTokens.size() > 2) {
        existingTokens.stream()
            .min(Comparator.comparing(Domain::getCreatedDate))
            .ifPresent(token -> refreshTokenRepository.deleteById(token.getId()));
      }

      refreshTokenRepository.save(RefreshToken.builder()
          .accountId(useCase.getAccountId())
          .token(useCase.getRefreshToken())
          .userIp(useCase.getUserIp())
          .userAgent(useCase.getUserAgent())
          .build());

    } else {
      matchedToken.update(useCase.getRefreshToken(), useCase.getUserIp(), useCase.getUserAgent());
      refreshTokenRepository.save(matchedToken);

    }
  }


}
