package com.backend.easy_to_web.security.authentication.manager;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import com.backend.easy_to_web.common.application.port.ClockHolder;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
public class JwtManager {

  public static final String BEARER = "Bearer";
  public static final String REFRESH_TOKEN = "refreshToken";
  public static final String ACCESS_TOKEN = "accessToken";
  public static final String AUTHORIZATION = "Authorization";

  public final long REFRESH_EXPIRE_TIME;
  private final long EXPIRE_TIME;
  private final String SECRET_KEY;

  private final ClockHolder clockHolder;

  public JwtManager(
      SecurityProperties securityProperties,
      ClockHolder clockHolder
  ) {
    this.SECRET_KEY = securityProperties.getSecretKey();
    this.EXPIRE_TIME = securityProperties.getExpireTime();
    this.REFRESH_EXPIRE_TIME = securityProperties.getRefreshExpireTime();
    this.clockHolder = clockHolder;
  }


  public String generateToken(String username,
      Collection<? extends GrantedAuthority> authorities,
      boolean isRefresh) {
    return generateToken(
        username,
        authorities,
        isRefresh ? getRefreshExpireDate() : getExpireDate()
    );

  }

  public String generateToken(Authentication authentication, boolean isRefresh) {
    return generateToken(
        (String) authentication.getPrincipal(),
        authentication.getAuthorities(),
        isRefresh ? getRefreshExpireDate() : getExpireDate()
    );
  }


  private String generateToken(
      String username,
      Collection<? extends GrantedAuthority> authorities,
      Date expiredDate
  ) {
    String convertAuth = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    //  setHeaderParam : claims와 관련된 컨텐츠, 형식, 암호화 작업에 대한 메타데이터를 제공, Map의 형식으로 여러개 가능
    // 나머지 : body 부분, 내용과 만료기간, 알고리즘을 설정
    return Jwts.builder()
//        .setHeaderParam("kid", "myKeyId")
        .setSubject(username)
        .claim("role", convertAuth)
        .setExpiration(expiredDate)
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }


  public Authentication getAuthentication(String accessToken) {
    return new UsernamePasswordAuthenticationToken(
        getUsername(accessToken),
        getTokenExpiredDate(accessToken),
        StringUtils.hasText(getRole(accessToken))
            ? createAuthorityList(getRole(accessToken))
            : List.of()
    );
  }


  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION);
    return resolveToken(bearerToken);
  }

  public String resolveToken(String bearerToken) {
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public String resolveRefreshToken(HttpServletRequest request) {
    if (ObjectUtils.isEmpty(request.getCookies()) || request.getCookies().length <= 0) {
      return null;
    }

    Cookie findCookie = Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN))
        .findFirst()
        .orElse(null);

    if (!ObjectUtils.isEmpty(findCookie)) {
      return findCookie.getValue();
    }

    return null;
  }

  public boolean validateToken(String accessToken) {
    if (accessToken == null) {
      return false;
    }

    try {
      return Jwts.parser()
          .setSigningKey(SECRET_KEY)
          .parseClaimsJws(accessToken)
          .getBody()
          .getExpiration()
          .after(clockHolder.getNow());

    } catch (Exception e) {
      return false;
    }
  }

  public JwtStatus statusToken(String accessToken) {
    try {
      if (accessToken == null) {
        return JwtStatus.WRONG_FORMAT;
      }

      boolean isValid = Jwts.parser()
          .setSigningKey(SECRET_KEY)
          .parseClaimsJws(accessToken)
          .getBody()
          .getExpiration()
          .after(clockHolder.getNow());

      if (!isValid) {
        return JwtStatus.EXPIRED;
      }

    } catch (SignatureException e) {
      return JwtStatus.WRONG_SIGNATURE;

    } catch (ExpiredJwtException e){
      return JwtStatus.EXPIRED;

    } catch (MalformedJwtException e){
      return JwtStatus.WRONG_FORMAT;

    } catch (UnsupportedJwtException e){
      return JwtStatus.UNSUPPORTED;

    } catch (PrematureJwtException e){
      return JwtStatus.PREMATURE;

    } catch (Exception e){
      return JwtStatus.ERROR_ETC;

    }

    return JwtStatus.VALID;
  }

  private Date getTokenExpiredDate(String accessToken) {
    return Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(accessToken)
        .getBody()
        .getExpiration();
  }

  private String getUsername(String accessToken) {
    return Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(accessToken)
        .getBody()
        .getSubject();
  }

  private String getRole(String accessToken) {
    return (String) Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(accessToken)
        .getBody()
        .get("role", String.class);

  }

  private Date getExpireDate() {
    Date now = clockHolder.getNow();
    return new Date(now.getTime() + EXPIRE_TIME);
  }

  private Date getRefreshExpireDate() {
    Date now = clockHolder.getNow();
    return new Date(now.getTime() + REFRESH_EXPIRE_TIME);
  }

  public enum JwtStatus {

    VALID,
    EXPIRED,
    WRONG_FORMAT,
    WRONG_SIGNATURE,
    PREMATURE,
    UNSUPPORTED,
    ERROR_ETC

  }

}
