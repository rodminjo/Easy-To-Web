package com.backend.easy_to_web.security.authentication.provider;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.service.AccountContext;
import com.backend.easy_to_web.security.authentication.token.AccountToken;
import com.backend.easy_to_web.security.authentication.token.AccountToken.AccountRecord;
import com.backend.easy_to_web.security.authentication.token.JwtLoginToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String requestId = (String) authentication.getPrincipal();
    String requestPassword = (String) authentication.getCredentials();

    // 계정 정보 찾은 후 비밀번호 인증
    AccountContext accountContext = (AccountContext) userDetailService.loadUserByUsername(requestId);
    if (!passwordEncoder.matches(requestPassword, accountContext.getPassword())) {
      throw new CustomAuthenticationException(ExceptionMessage.LOGIN_INFO_INVALID);
    }

    LoginResult result = accountContext.getAccount();
    String accountId = result.getId().toString();

    return new AccountToken(
        accountId,
        result,
        new AccountRecord(
            accountId,
            result.getNickname(),
            result.getEmail(),
            result.getProfileUrl()
        ),
        accountContext.getAuthorities()
    );
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(JwtLoginToken.class);
  }
}
