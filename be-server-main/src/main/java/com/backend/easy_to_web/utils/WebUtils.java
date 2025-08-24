package com.backend.easy_to_web.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.util.StringUtils;

public class WebUtils {

  public static String getUserAgent(HttpServletRequest request) {
    return request.getHeader("User-Agent");
  }

  public static String getClientIp(HttpServletRequest request) {
    List<String> headerNames = List.of(
        "X-Forwarded-For", "x-real-ip", "x-original-forwarded-for",
        "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
    );

    return headerNames.stream()
        .map(request::getHeader)
        .filter(ip -> StringUtils.hasText(ip) && !ip.equalsIgnoreCase("unknown"))
        .findFirst()
        .orElse(request.getRemoteAddr());
  }

  public static String convertForUrl(String target){
    return URLEncoder.encode(target, StandardCharsets.UTF_8)
        .replaceAll("\\+", "%20");
  }
}
