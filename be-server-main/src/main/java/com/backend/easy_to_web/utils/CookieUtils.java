package com.backend.easy_to_web.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieUtils {


  public static Optional<String> getCookieValue(HttpServletRequest request, String name) {
    if (request.getCookies() == null) return Optional.empty();
    for (Cookie c : request.getCookies()) {
      if (name.equals(c.getName())) return Optional.of(c.getValue());
    }
    return Optional.empty();
  }

  public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
    ResponseCookie cookie = ResponseCookie.from(name, value)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(maxAgeSeconds)
        .sameSite("Lax")
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  public static void deleteCookie(HttpServletResponse response, String name) {
    ResponseCookie cookie = ResponseCookie.from(name, "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .sameSite("Lax")
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
