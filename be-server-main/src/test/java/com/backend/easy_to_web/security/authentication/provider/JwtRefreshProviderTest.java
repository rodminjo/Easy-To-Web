package com.backend.easy_to_web.security.authentication.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.easy_to_web.common.application.handler.BeanAwareUseCasePublisher;
import com.backend.easy_to_web.common.application.handler.UseCaseHandlerRegistry;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.common.application.port.ClockHolder;
import com.backend.easy_to_web.common.domain.exception.CustomAuthenticationException;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.security.authentication.token.AccountToken;
import com.backend.easy_to_web.security.authentication.token.JwtRefreshToken;
import com.backend.easy_to_web.test_support.mock.account.application.FakeRefreshTokenCheckHandler;
import com.backend.easy_to_web.test_support.mock.properties.PropertiesContainer;
import com.backend.easy_to_web.test_support.mock.utils.MockClockHolder;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


class JwtRefreshProviderTest extends UnitTest{

  public final String email = "test@example.com";
  private final PropertiesContainer propertiesContainer = new PropertiesContainer();
  private final FakeRefreshTokenCheckHandler handler = new FakeRefreshTokenCheckHandler();
  private final UseCaseHandlerRegistry registry = new UseCaseHandlerRegistry(List.of(handler), List.of(), List.of());
  private final UseCasePublisher publisher = new BeanAwareUseCasePublisher(registry);

  private JwtManager getJwtManager() {
    SecurityProperties properties = propertiesContainer.getSecurityProperties();
    ClockHolder clockHolder = new MockClockHolder(new Date());
    return new JwtManager(properties, clockHolder);
  }

  @Test
  @DisplayName("credentials 내부 refresh 토큰이 유효하지 않으면 오류가 발생된다")
  void shouldThrowExceptionWhenRefreshTokenInvalid(){
    // given
    JwtRefreshProvider refreshProvider = new JwtRefreshProvider(getJwtManager(), publisher);
    TestingAuthenticationToken token = new TestingAuthenticationToken(email, "");
    // when

    // then
    assertThatThrownBy(() -> refreshProvider.authenticate(token))
        .isExactlyInstanceOf(CustomAuthenticationException.class)
        .isInstanceOf(AuthenticationException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_EXPIRED.getDescription());

  }

  @Test
  @DisplayName("인증정보를 반환한다")
  void successIfValid(){
    // given
    JwtManager jwtManager = getJwtManager();
    JwtRefreshProvider refreshProvider = new JwtRefreshProvider(jwtManager, publisher);
    TestingAuthenticationToken token = new TestingAuthenticationToken(email, "");
    String testToken = jwtManager.generateToken(token, true);

    // when
    Authentication authenticate =
        refreshProvider.authenticate(new JwtRefreshToken(testToken, ""));

    // then
    assertThat(authenticate).isExactlyInstanceOf(AccountToken.class);
    assertThat(authenticate.getPrincipal()).isEqualTo(email);
  }
}