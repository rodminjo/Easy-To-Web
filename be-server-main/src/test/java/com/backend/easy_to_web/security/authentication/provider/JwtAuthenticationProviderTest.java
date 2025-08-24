package com.backend.easy_to_web.security.authentication.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.easy_to_web.account.domain.result.LoginResult;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.token.JwtLoginToken;
import com.backend.easy_to_web.test_support.mock.FakeUserDetailService;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@TestInstance(Lifecycle.PER_CLASS)
class JwtAuthenticationProviderTest extends UnitTest{

  private final UUID testId = UUID.randomUUID();
  private final String testEmail = "test@example.com";
  private final String testPassword = "password";

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private JwtAuthenticationProvider testProvider;


  @BeforeAll
  void setup() {
    LoginResult testData = LoginResult.builder()
        .id(testId)
        .email(testEmail)
        .password(passwordEncoder.encode(testPassword))
        .nickname("testUser")
        .profileUrl("https://example.com/profile.jpg")
        .roles(AuthorityUtils.createAuthorityList(List.of("ROLE_USER")))
        .build();

    UserDetailsService userDetailsService = new FakeUserDetailService(testData);
    testProvider = new JwtAuthenticationProvider(userDetailsService, passwordEncoder);
  }

  @Test
  @DisplayName("비밀번호가 틀리면 CustomAuthenticationException이 발생한다")
  void passwordInvalidTest() {
    // given
    String id = "test@example.com";
    String wrongPassword = "wrongPassword";

    // when & then
    assertThatThrownBy(() -> testProvider.authenticate(new JwtLoginToken(id, wrongPassword)))
        .isExactlyInstanceOf(CustomAuthenticationException.class)
        .hasMessage(ExceptionMessage.LOGIN_INFO_INVALID.getDescription());
  }

  @Test
  @DisplayName("비밀번호가 맞으면 인증정보를 반환한다")
  void passwordValidTest() {
    // given

    // when
    Authentication authenticate = testProvider.authenticate(new JwtLoginToken(testEmail, testPassword));

    // then
    assertThat(authenticate.getPrincipal()).isEqualTo(testId.toString());
    assertThat(authenticate.getCredentials()).isExactlyInstanceOf(LoginResult.class);
    assertThat(authenticate.getAuthorities()).hasSize(1);
  }
}