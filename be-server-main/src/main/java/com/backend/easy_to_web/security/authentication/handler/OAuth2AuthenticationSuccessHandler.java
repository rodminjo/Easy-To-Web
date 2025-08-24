package com.backend.easy_to_web.security.authentication.handler;

import com.backend.easy_to_web.account.domain.usecase.MergeRefreshTokenUseCase;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.infrastructure.controller.BaseController;
import com.backend.easy_to_web.config.properties.ServerProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.backend.easy_to_web.security.authentication.service.OAuthAccountContext;
import com.backend.easy_to_web.security.authentication.token.AccountToken;
import com.backend.easy_to_web.utils.CookieUtils;
import com.backend.easy_to_web.utils.SecurityScopeUtils;
import com.backend.easy_to_web.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends BaseController implements AuthenticationSuccessHandler {

  private final ServerProperties serverProperties;
  private final JwtManager jwtManager;
  private final UseCasePublisher publisher;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {

    // Principal 추출
    OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
    OAuthAccountContext principal = (OAuthAccountContext) oauth.getPrincipal();

    // oauth token -> account Token
    setCustomAuthenticationFromOAuth2(principal);

    // JWT 생성
    final String refreshToken = jwtManager.generateToken(principal.getAccount().getId().toString(), principal.getAccount().getRoles(), true);

    // 리프레시 토큰 저장/병합 (accountId는 프로젝트 타입에 맞춰 조정)
    MergeRefreshTokenUseCase command = new MergeRefreshTokenUseCase(
        principal.getAccount().getId().toString(),
        refreshToken,
        WebUtils.getClientIp(request),
        WebUtils.getUserAgent(request)
    );
    publisher.publish(command);

    // 응답 헤더/쿠키 세팅
    setRefreshToken(response, refreshToken, jwtManager.REFRESH_EXPIRE_TIME);

    // 2) 리다이렉트 (바디/Writer 사용 금지)
    String next = CookieUtils.getCookieValue(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME).orElse("/");
    if (!next.startsWith("/")){
      next = "/" + next;
    }
    response.sendRedirect(serverProperties.getFeDomain() + next);
  }


  private static void setCustomAuthenticationFromOAuth2(OAuthAccountContext principal) {
    AccountToken token = new AccountToken(
        principal.getAccount().getId(),
        null,
        null,
        principal.getAuthorities()
    );
    SecurityScopeUtils.setAuthentication(token);
  }


}
