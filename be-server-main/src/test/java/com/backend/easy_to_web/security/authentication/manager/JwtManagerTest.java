package com.backend.easy_to_web.security.authentication.manager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.backend.easy_to_web.common.application.port.ClockHolder;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.test_support.mock.utils.MockClockHolder;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;


class JwtManagerTest extends UnitTest{

  private final long defaultExpiredTime = 3 * 24 * 3600 * 1000L;
  private final String email = "test@example.com";
  private final List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList(List.of("ROLE_USER"));
  public final TestingAuthenticationToken token = new TestingAuthenticationToken(email, "", roles);

  private JwtManager setupJwtManager(long expiredTime, ClockHolder clockHolder) {
    String testSecretKey = "123412341234123412341234123412342374ijdskfg.";
    SecurityProperties properties = new SecurityProperties(testSecretKey, expiredTime, expiredTime);
    return new JwtManager(properties, clockHolder);
  }


  @Test
  @DisplayName("authentication으로_jwt토큰을_생성한다")
  void jwtTokenCreateByAuthenticationTest() {
    // given
    ClockHolder clockHolder = new MockClockHolder(new Date());
    JwtManager jwtManager = setupJwtManager(defaultExpiredTime, clockHolder);

    // when
    String accessToken = jwtManager.generateToken(token, false);
    Authentication accessAuthentication = jwtManager.getAuthentication(accessToken);

    String refreshToken = jwtManager.generateToken(token, true);
    Authentication refreshAuthentication = jwtManager.getAuthentication(refreshToken);

    // then
    validateToken(accessToken, accessAuthentication);
    validateToken(refreshToken, refreshAuthentication);
  }

  @Test
  @DisplayName("token 유효성 검사 테스트")
  void tokenValidationTests(){
    ClockHolder clockHolder = new MockClockHolder(new Date());
    JwtManager jwtManager = setupJwtManager(defaultExpiredTime, clockHolder);
    String validToken = jwtManager.generateToken(token, false);

    JwtManager expiredJwtManager = setupJwtManager(1L, clockHolder);
    String expiredToken = expiredJwtManager.generateToken(token, false);

    // when
    boolean validTokenCheck = jwtManager.validateToken(validToken);
    boolean nullCheck = jwtManager.validateToken(null);
    boolean emptyCheck = jwtManager.validateToken("");
    boolean invalidCheck = jwtManager.validateToken("1293ilkasdfn;");
    boolean expiredTokenCheck = jwtManager.validateToken(expiredToken);

    // then
    assertThat(validTokenCheck).isTrue();
    assertThat(nullCheck).isFalse();
    assertThat(emptyCheck).isFalse();
    assertThat(invalidCheck).isFalse();
    assertThat(expiredTokenCheck).isFalse();
  }

  @Test
  @DisplayName("request에서_refresh토큰을_꺼내온다")
  void getRefreshTokenInRequest(){
    //given
    String refreshToken = "refresh_token";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie(JwtManager.REFRESH_TOKEN, refreshToken));
    ClockHolder clockHolder = new MockClockHolder(new Date());
    JwtManager jwtManager = setupJwtManager(defaultExpiredTime, clockHolder);

    // when
    String getRefreshToken = jwtManager.resolveRefreshToken(request);

    // then
    assertThat(getRefreshToken).isEqualTo(refreshToken);
  }

  @Test
  @DisplayName("request에서_access토큰을_꺼내온다")
  void getAccessTokenInRequest(){
    //given
    String accessToken = "access_token";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization",JwtManager.BEARER + " " + accessToken);
    ClockHolder clockHolder = new MockClockHolder(new Date());
    JwtManager jwtManager = setupJwtManager(defaultExpiredTime, clockHolder);

    // when
    String getAccessToken = jwtManager.resolveToken(request);

    // then
    assertThat(getAccessToken).isEqualTo(accessToken);
  }


  private void validateToken(String token, Authentication authentication) {
    assertThat(StringUtils.hasText(token)).isTrue();
    assertThat(authentication.getPrincipal()).isEqualTo(email);
    assertThat(authentication.getAuthorities().size()).isOne();
    assertThat(authentication.getAuthorities().stream().findFirst().orElse(null))
        .isEqualTo(roles.getFirst());
    assertThat(((Date) authentication.getCredentials()).after(new Date())).isTrue();
  }
}