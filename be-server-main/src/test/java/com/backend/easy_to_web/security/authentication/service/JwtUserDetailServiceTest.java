package com.backend.easy_to_web.security.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.easy_to_web.common.application.handler.BeanAwareUseCasePublisher;
import com.backend.easy_to_web.common.application.handler.UseCaseHandlerRegistry;
import com.backend.easy_to_web.common.application.handler.UseCasePublisher;
import com.backend.easy_to_web.test_support.mock.account.application.FakeAccountLoginHandler;
import com.backend.easy_to_web.test_support.model.UnitTest;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@TestInstance(Lifecycle.PER_CLASS)
class JwtUserDetailServiceTest extends UnitTest{
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final FakeAccountLoginHandler handler = new FakeAccountLoginHandler(passwordEncoder);
  private final UseCaseHandlerRegistry registry = new UseCaseHandlerRegistry(List.of(handler), List.of(), List.of());
  private final UseCasePublisher publisher = new BeanAwareUseCasePublisher(registry);
  private final JwtUserDetailService jwtUserDetailService = new JwtUserDetailService(publisher);


  @Test
  @DisplayName("정상적으로 계정을 가져온다면 AccountContext를 생성하여 반환한다")
  void loadAccountContextByUserName(){
    // given
    String email = "test@example.com";

    // when & then
    UserDetails result = jwtUserDetailService.loadUserByUsername(email);
    assertThat(result).isExactlyInstanceOf(AccountContext.class);
    assertThat(((AccountContext) result).getAccount().getNickname()).isEqualTo("testUser");
    assertThat(((AccountContext) result).getAccount().getEmail()).isEqualTo(email);
    assertThat(result.getUsername()).isEqualTo(email);
    assertThat(passwordEncoder.matches("password", result.getPassword())).isTrue();
    assertThat(result.getAuthorities().size()).isOne();
    assertThat(Objects.requireNonNull(result.getAuthorities().stream().findFirst().orElse(null)).getAuthority())
        .isEqualTo("ROLE_USER");
  }

}