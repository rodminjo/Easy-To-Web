package com.backend.easy_to_web.security.authentication.filter;


import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.token.JwtRefreshToken;
import com.backend.easy_to_web.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;


/**
 * JWT 리프레시 인증 필터 URL : /api/account/reissue
 */
public class JwtRefreshProcessingFilter extends AbstractAuthenticationProcessingFilter {

  private final JwtManager jwtManager;


  public JwtRefreshProcessingFilter(AuthenticationManager authenticationManager,
      JwtManager jwtManager) {
    super(new AntPathRequestMatcher("/api/account/reissue", HttpMethod.POST.name()), authenticationManager);
    this.jwtManager = jwtManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    // Jwt refresh 요청인지 검증후 처리
    String refreshToken = jwtManager.resolveRefreshToken(request);

    // refreshToken 이 존재하는지 확인
    if (!StringUtils.hasText(refreshToken)) {
      throw new CustomAuthenticationException(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND);
    }

    // user Agent 가 존재하는지 확인
    if (!StringUtils.hasText(WebUtils.getUserAgent(request))){
      throw new CustomAuthenticationException(ExceptionMessage.USER_AGENT_INVALID);
    }

    JwtRefreshToken jwtRefreshToken = new JwtRefreshToken(refreshToken, "", List.of());

    return getAuthenticationManager().authenticate(jwtRefreshToken);
  }
}
