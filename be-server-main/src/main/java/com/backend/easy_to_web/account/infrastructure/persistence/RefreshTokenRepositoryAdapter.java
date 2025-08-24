package com.backend.easy_to_web.account.infrastructure.persistence;

import com.backend.easy_to_web.account.domain.model.RefreshToken;
import com.backend.easy_to_web.account.domain.port.out.RefreshTokenRepository;
import com.backend.easy_to_web.account.infrastructure.persistence.entity.RefreshTokenEntity;
import com.backend.easy_to_web.account.infrastructure.persistence.jpa.RefreshTokenJpaRepository;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.domain.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

  private final RefreshTokenJpaRepository refreshTokenJpaRepository;

  @Override
  public List<RefreshToken> getAllByAccountId(String accountId) {
    List<RefreshToken> list = findAllByAccountId(accountId);

    if (list.isEmpty()){
      throw new ResourceNotFoundException(ExceptionMessage.INFO_REFRESH_TOKEN_NOT_FOUND);
    }
    return list;
  }


  @Override
  public List<RefreshToken> findAllByAccountId(String accountId) {
    return refreshTokenJpaRepository.findAllByAccountId(accountId).stream()
        .map(RefreshTokenEntity::toDomain)
        .toList();
  }

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    return refreshTokenJpaRepository.save(RefreshTokenEntity.of(refreshToken)).toDomain();
  }

  @Override
  public void deleteById(long id) {
    refreshTokenJpaRepository.deleteById(id);
  }
}
