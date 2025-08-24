package com.backend.easy_to_web.security.authentication.repository;

import com.backend.easy_to_web.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  public static final String AUTH_REQUEST_COOKIE_NAME = "OAUTH2_AUTHZ_REQUEST";
  public static final String REDIRECT_URI_COOKIE_NAME = "OAUTH2_NEXT";
  public static final int COOKIE_EXPIRE_SECONDS = 600; // 10분

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return CookieUtils.getCookieValue(request, AUTH_REQUEST_COOKIE_NAME)
        .map(this::deserialize)
        .orElse(null);
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (authorizationRequest == null) {
      CookieUtils.deleteCookie(response, AUTH_REQUEST_COOKIE_NAME);
      CookieUtils.deleteCookie(response, REDIRECT_URI_COOKIE_NAME);
      return;
    }

    // 직렬화 후 저장
    String serialized = serialize(authorizationRequest);
    CookieUtils.addCookie(response, AUTH_REQUEST_COOKIE_NAME, serialized, COOKIE_EXPIRE_SECONDS);

    // next 파라미터가 있으면 저장
    String next = request.getParameter("next");
    if (next != null && !next.isBlank()) {
      CookieUtils.addCookie(response, REDIRECT_URI_COOKIE_NAME, next, COOKIE_EXPIRE_SECONDS);
    }
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    OAuth2AuthorizationRequest req = loadAuthorizationRequest(request);
    CookieUtils.deleteCookie(response, AUTH_REQUEST_COOKIE_NAME);
    return req;
  }

  private String serialize(OAuth2AuthorizationRequest obj) {
    byte[] bytes = SerializationUtils.serialize(obj);
    return Base64.getUrlEncoder().encodeToString(bytes);
  }

  private OAuth2AuthorizationRequest deserialize(String value) {
    byte[] bytes = Base64.getUrlDecoder().decode(value);
    return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(bytes);
  }


}