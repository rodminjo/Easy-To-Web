package com.backend.easy_to_web.account.domain.port.out;

import com.backend.easy_to_web.account.domain.model.RefreshToken;
import java.util.List;

public interface RefreshTokenRepository {

  List<RefreshToken> getAllByAccountId(String accountId);

  List<RefreshToken> findAllByAccountId(String accountId);

  RefreshToken save(RefreshToken refreshToken);

  void deleteById(long id);

}
