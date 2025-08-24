package com.backend.easy_to_web.security.authentication.filter;

import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountLoginRest.AccountLoginInput;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.token.JwtLoginToken;
import com.backend.easy_to_web.utils.MapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

/**
 * JWT 로그인 인증 필터 URL : /api/account/login
 */
public class JwtLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

  public JwtLoginProcessingFilter(AuthenticationManager authenticationManager) {
    super(new AntPathRequestMatcher("/api/account/login", HttpMethod.POST.name(), true), authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

    // 로그인 요청
    AccountLoginInput loginInput;
    try {
      loginInput = MapperUtils.getMapper()
          .readValue(request.getInputStream(), AccountLoginInput.class);
    } catch (Exception e) {
      loginInput = new AccountLoginInput("", "");
    }
    if (!StringUtils.hasText(loginInput.email()) || !StringUtils.hasText(loginInput.password())) {
      throw new CustomAuthenticationException(ExceptionMessage.LOGIN_INFO_NOT_FOUND);
    }

    // 토큰 생성하여 인증 요청을 처리할 매니저에게 전달
    JwtLoginToken token = new JwtLoginToken(loginInput.email(), loginInput.password());
    return getAuthenticationManager().authenticate(token);

  }
}
