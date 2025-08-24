package com.backend.easy_to_web.security.authentication.service;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.account.domain.usecase.LoginUseCase;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailService implements UserDetailsService {

  private final UseCasePublisher useCasePublisher;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 계정 정보 찾기
    // account -> userDetails 형태로 변환해서 보내주기
    LoginResult result = useCasePublisher.publish(LoginResult.class, new LoginUseCase(username));
    return new AccountContext(result, result.getRoles());
  }
}
