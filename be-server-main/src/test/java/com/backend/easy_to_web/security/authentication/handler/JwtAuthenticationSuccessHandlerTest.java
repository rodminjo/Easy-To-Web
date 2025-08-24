package com.backend.easy_to_web.security.authentication.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.easy_to_web.common.application.handler.BeanAwareUseCasePublisher;
import com.backend.easy_to_web.common.application.handler.UseCaseHandlerRegistry;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.application.port.ClockHolder;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.test_support.mock.account.application.FakeRefreshTokenMergeHandler;
import com.backend.easy_to_web.test_support.mock.utils.MockClockHolder;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;


@TestInstance(value = Lifecycle.PER_CLASS)
class JwtAuthenticationSuccessHandlerTest extends UnitTest{

  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();



  private final String email = "test@example.com";
  private JwtManager jwtManager;

  @BeforeAll
  void setup() throws ServletException, IOException {
    var handlerForRegister = new FakeRefreshTokenMergeHandler();
    long expiredTime = 60 * 60 * 1000 * 24L;
    String testSecretKey = "123412341234123412341234123412342374ijdskfg.";

    List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList(List.of("ROLE_USER"));
    TestingAuthenticationToken token = new TestingAuthenticationToken(email, null, roles);
    SecurityProperties properties = new SecurityProperties(testSecretKey, expiredTime, expiredTime);
    ClockHolder clockHolder = new MockClockHolder(new Date());
    jwtManager = new JwtManager(properties, clockHolder);

    UseCaseHandlerRegistry registry = new UseCaseHandlerRegistry(List.of(handlerForRegister), List.of(), List.of());
    UseCasePublisher publisher = new BeanAwareUseCasePublisher(registry);
    JwtAuthenticationSuccessHandler handler = new JwtAuthenticationSuccessHandler(jwtManager, publisher);
    handler.onAuthenticationSuccess(request, response, token);
  }

  @Test
  @DisplayName("Response 헤더에 accessToken을 담는다")
  void shouldContainAccessTokenInResponseHeader() {
    String header = response.getHeader(JwtManager.AUTHORIZATION);
    String accessToken = jwtManager.resolveToken(header);
    Authentication authentication = jwtManager.getAuthentication(accessToken);

    assertThat(header).isNotBlank();
    assertThat(authentication.getPrincipal()).isEqualTo(email);
  }

  @Test
  @DisplayName("Response 헤더에 refreshToken set-cookie를 담는다")
  void shouldContainRefreshTokenInResponseHeader() {
    String header = response.getHeader(HttpHeaders.SET_COOKIE);
    assertThat(header).isNotBlank();
  }

  @Test
  @DisplayName("Response에 상태코드와 contentType을 저장한다")
  void shouldSetStatusCodeAndContentTypeInResponse() {
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
  }

  @Test
  @DisplayName("Response body에 tokenDto를 저장한다")
  void shouldContainTokenDtoInResponseBody() throws UnsupportedEncodingException {
    // 응답 본문을 문자열로 변환
    String responseBody = response.getContentAsString();

    // 응답 본문에 각 필드 값이 포함되어 있는지 확인
    assertThat(responseBody).contains("tokenType");
    assertThat(responseBody).contains("accessToken");
    assertThat(responseBody).contains("refreshToken");
  }
}
