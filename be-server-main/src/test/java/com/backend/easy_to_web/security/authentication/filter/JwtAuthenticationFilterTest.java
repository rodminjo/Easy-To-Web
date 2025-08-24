package com.backend.easy_to_web.security.authentication.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.easy_to_web.common.application.port.ClockHolder;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.test_support.mock.properties.PropertiesContainer;
import com.backend.easy_to_web.test_support.mock.utils.MockClockHolder;
import com.backend.easy_to_web.test_support.model.UnitTest;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;


class JwtAuthenticationFilterTest extends UnitTest{

  private final PropertiesContainer propertiesContainer = new PropertiesContainer();

  private final String email = "test@example.com";
  private final List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList(List.of("ROLE_USER"));
  private final TestingAuthenticationToken token = new TestingAuthenticationToken(email, "", roles);

  private JwtManager setupJwtManager() {
    ClockHolder clockHolder = new MockClockHolder(new Date());
    SecurityProperties properties = propertiesContainer.getSecurityProperties();
    return new JwtManager(properties, clockHolder);
  }

  private MockHttpServletRequest createMockRequest(String token) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(JwtManager.AUTHORIZATION, JwtManager.BEARER + " " + token);
    return request;
  }

  private void assertAuthentication(Authentication authentication, String email, List<GrantedAuthority> roles) {
    assertThat(authentication.getPrincipal()).isEqualTo(email);
    assertThat(authentication.getAuthorities().size()).isEqualTo(roles.size());
    assertThat(authentication.getAuthorities().stream().findFirst().orElse(null)).isEqualTo(roles.getFirst());
  }

  @Test
  @DisplayName("access_토큰이_존재하고_유효할_경우_SecurityContext에_담는다")
  void saveInSecurityContextIfAccessTokenValidTest() throws ServletException, IOException {
    // given
    JwtManager jwtManager = setupJwtManager();
    String accessToken = jwtManager.generateToken(token, false);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = createMockRequest(accessToken);
    MockFilterChain filterChain = mock(MockFilterChain.class);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtManager);

    // when
    jwtAuthenticationFilter.doFilter(request, response, filterChain);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // then
    assertAuthentication(authentication, email, roles);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("access_토큰이_유효히지_않을_경우_인증없이_다음필터로_이동한다")
  void notSaveAndContinueIfAccessTokenInvalidTest() throws ServletException, IOException {
    // given
    String invalidToken = "invalid_token";
    JwtManager jwtManager = setupJwtManager();

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = createMockRequest(invalidToken);
    MockFilterChain filterChain = mock(MockFilterChain.class);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtManager);

    // when
    jwtAuthenticationFilter.doFilter(request, response, filterChain);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // then
    assertThat(authentication).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }
}