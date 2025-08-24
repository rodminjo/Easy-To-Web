package com.backend.easy_to_web.security.authentication.handler;

import com.backend.easy_to_web.account.domain.usecase.MergeRefreshTokenUseCase;
import com.backend.easy_to_web.account.infrastructure.controller.rest.TokenRest.TokenOutput;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.infrastructure.controller.BaseController;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.token.AccountToken;
import com.backend.easy_to_web.security.authentication.token.AccountToken.AccountRecord;
import com.backend.easy_to_web.utils.MapperUtils;
import com.backend.easy_to_web.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationSuccessHandler extends BaseController implements AuthenticationSuccessHandler {

  private final JwtManager jwtManager;
  private final UseCasePublisher publisher;

  /**
   * jwt 인증 필터에게서 받아온 UsernamePasswordAuthenticationToken 을 받아 처리하는 handler
   * @param request the request which caused the successful authentication
   * @param response the response
   * @param authentication provider가 생성한 UsernamePasswordAuthenticationToken
   * the authentication process.
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // jwt token을 생성해서 response 로 반환
    String tokenType = JwtManager.BEARER;
    String jwtToken = jwtManager.generateToken(authentication, false);
    String jwtRefreshToken = jwtManager.generateToken(authentication, true);

    MergeRefreshTokenUseCase command = new MergeRefreshTokenUseCase(
        authentication.getPrincipal().toString(),
        jwtRefreshToken,
        WebUtils.getClientIp(request),
        WebUtils.getUserAgent(request)
    );

    // jwt refreshToken save or update
    publisher.publish(command);

    // response setting
    setRefreshToken(response, jwtRefreshToken, jwtManager.REFRESH_EXPIRE_TIME);
    response.setHeader(JwtManager.AUTHORIZATION, tokenType + " " + jwtToken);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.OK.value());

    // set tokenDto
    AccountToken token = (authentication instanceof AccountToken) ? (AccountToken) authentication : null;
    AccountRecord dto = (token != null) && (token.getAccount() != null) ? token.getAccount() : null;

    TokenOutput output = TokenOutput.builder()
        .tokenType(tokenType)
        .accessToken(jwtToken)
        .refreshToken(jwtRefreshToken)
        .account(dto)
        .build();
    setBodyTokenOutput(response, output);
  }


  private void setBodyTokenOutput(HttpServletResponse response, TokenOutput output)
      throws IOException {

    // getWriter 할 경우 더이상 response 변경 안됨. 유의!
    Response<TokenOutput> respond = respond(output);
    MapperUtils.getMapper().writeValue(response.getWriter(), respond);
  }


}
