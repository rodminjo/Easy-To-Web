package com.backend.easy_to_web.Integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.backend.easy_to_web.account.infrastructure.controller.rest.AccountLoginRest.AccountLoginInput;
import com.backend.easy_to_web.account.infrastructure.controller.rest.TokenRest.TokenOutput;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.common.infrastructure.controller.rest.Response;
import com.backend.easy_to_web.config.properties.SecurityProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.test_support.config.db.DatabaseClearExtension;
import com.backend.easy_to_web.test_support.model.IntegrationTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

@ExtendWith({DatabaseClearExtension.class})
@Sql(value = "classpath:sql/AccountRestControllerIntegrationTest.sql")
@AutoConfigureMockMvc(addFilters = false)
public class SecurityIntegrationTest extends IntegrationTest {

  @Autowired
  JwtManager jwtManager;

  @Autowired
  SecurityProperties securityProperties;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    this.webTestClient = MockMvcWebTestClient
        .bindToApplicationContext(context)
        .apply(springSecurity())
        .configureClient()
//          .filter(basicAuthentication("user", "password"))
        .build();
  }


  @Test
  @DisplayName("로그인 - 비밀번호, id가 빈값이면 LOGIN_INFO_NOT_FOUND 에러를 반환한다")
  void shouldReturnValidationErrorsWhenEmptyValues() {
    // given
    String email = "";
    String password = "";
    ExceptionMessage expectedError = ExceptionMessage.LOGIN_INFO_NOT_FOUND;
    AccountLoginInput input = new AccountLoginInput(email, password);

    // when & then
    sendPostRequest("/api/account/login", input)
        // then: 응답 검증
        .expectStatus().isEqualTo(expectedError.getHttpStatus())
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        }) // 응답 본문 타입 지정
        .consumeWith(response -> {
          errorDataValidate(response, expectedError);
        });
  }

  @Test
  @DisplayName("로그인 - 비밀번호나 id가 틀렸다면 에러를 반환한다")
  void shouldReturnValidationErrorsWhenInvalidValues() {
    // given
    String email = "example1@gmail.com";
    String wrongPassword = "password_hash";

    String wrongEmail = "example_wrong@gmail.com";
    String password = "password_hash_1";

    ExceptionMessage expectedError = ExceptionMessage.LOGIN_INFO_INVALID;
    AccountLoginInput input1 = new AccountLoginInput(email, wrongPassword);
    AccountLoginInput input2 = new AccountLoginInput(wrongEmail, password);

    // when && then
    sendPostRequest("/api/account/login", input1)
        // then: 응답 검증
        .expectStatus().isEqualTo(expectedError.getHttpStatus())
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        }) // 응답 본문 타입 지정
        .consumeWith(response -> {
          errorDataValidate(response, expectedError);
        });

    sendPostRequest("/api/account/login", input2)
        // then: 응답 검증
        .expectStatus().isEqualTo(expectedError.getHttpStatus())
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        }) // 응답 본문 타입 지정
        .consumeWith(response -> {
          errorDataValidate(response, expectedError);
        });
  }

  @Test
  @DisplayName("로그인 - 정보가 일치하면 accessToken 과 refreshToken을 얻는다")
  void shouldReturnTokensWhenValidCredentials() {
    // given
    String email = "example1@gmail.com";
    String password = "password_hash_1";
    AccountLoginInput input1 = new AccountLoginInput(email, password);

    // when && then
    sendPostRequest("/api/account/login", input1)
        // then: 응답 검증
        .expectStatus().isEqualTo(HttpStatus.OK)
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        })
        .consumeWith(response -> {
          successDataValidate(response);
          TokenOutput data = Objects.requireNonNull(response.getResponseBody()).getData();
          assertThat(jwtManager.validateToken(data.accessToken())).isTrue();
          assertThat(jwtManager.validateToken(data.refreshToken())).isTrue();

          List<ResponseCookie> refreshTokenCookies = response.getResponseCookies()
              .get(JwtManager.REFRESH_TOKEN);
          assertThat(refreshTokenCookies.size()).isOne();

          ResponseCookie cookie = refreshTokenCookies.get(0);
          assertThat(cookie.isHttpOnly()).isTrue();
          assertThat(cookie.getPath()).isEqualTo("/api/account/reissue");
          assertThat(jwtManager.validateToken(cookie.getValue())).isTrue();
        });
  }

  @Test
  @DisplayName("토큰재발급 - refreshToken이 존재하지 않는다면 에러를 반환한다")
  void shouldReturnErrorWhenNoRefreshToken() {
    // given
    ExceptionMessage expectedError = ExceptionMessage.REFRESH_TOKEN_NOT_FOUND;

    // when && then
    sendPostRequest("/api/account/reissue", "")
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        })
        .consumeWith(response -> {
          errorDataValidate(response, expectedError);
        });
  }

  @Test
  @DisplayName("토큰재발급 - refreshToken이 유효하지 않거나 만료되었다면 에러를 반환한다")
  void shouldReturnErrorWhenExpiredRefreshToken() {
    // given
    String expiredRefreshToken = getRefreshToken(new Date());
    String invalidRefreshToken = getRefreshToken(new Date(new Date().getTime() + 1000000000)) + "invalid";
    ExceptionMessage expectedError = ExceptionMessage.REFRESH_TOKEN_EXPIRED;

    // when && then
    sendPostRequestWithCookie("/api/account/reissue", JwtManager.REFRESH_TOKEN, expiredRefreshToken)
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        })
        .consumeWith(response -> {
          errorDataValidate(response, expectedError);
        });

    sendPostRequestWithCookie("/api/account/reissue", JwtManager.REFRESH_TOKEN, invalidRefreshToken)
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        })
        .consumeWith(response -> {
          errorDataValidate(response, expectedError);
        });
  }

  @Test
  @DisplayName("토큰재발급 - refreshToken이 DB에 존재하지 않는다면 에러를 반환한다")
  void shouldReturnErrorWhenNonexistentRefreshToken() {
    // given
    String refreshToken = getRefreshToken(new Date(new Date().getTime() + 1000000000));
    ExceptionMessage expectedError = ExceptionMessage.REFRESH_TOKEN_INVALID;


    // when && then
    sendPostRequestWithCookie("/api/account/reissue", JwtManager.REFRESH_TOKEN, refreshToken)
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        })
        .consumeWith(response -> {
          errorDataValidate(response, expectedError);
        });
  }

  @Test
  @DisplayName("토큰재발급 - refreshToken이 유효하다면 새로운 token을 얻는다")
  void shouldReturnNewTokensWhenValidRefreshToken() {
    // given
    String refreshToken = setupAndGetRefreshToken();

    // when && then
    sendPostRequestWithCookie("/api/account/reissue", JwtManager.REFRESH_TOKEN, refreshToken)
        .expectStatus().isEqualTo(HttpStatus.OK)
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {
        })
        .consumeWith(response -> {
          successDataValidate(response);
          TokenOutput data = Objects.requireNonNull(response.getResponseBody()).getData();
          assertThat(jwtManager.validateToken(data.accessToken())).isTrue();
          assertThat(jwtManager.validateToken(data.refreshToken())).isTrue();

          List<ResponseCookie> refreshTokenCookies = response.getResponseCookies()
              .get(JwtManager.REFRESH_TOKEN);
          assertThat(refreshTokenCookies.size()).isOne();

          ResponseCookie cookie = refreshTokenCookies.get(0);
          assertThat(cookie.isHttpOnly()).isTrue();
          assertThat(cookie.getPath()).isEqualTo("/api/account/reissue");
          assertThat(jwtManager.validateToken(cookie.getValue())).isTrue();
        });
  }

  private ResponseSpec sendPostRequest(String path, Object input) {
    return webTestClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(path)
            .build()
        )
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
        .header("User-Agent", "Mozilla/5.0 (compatible;)")
        .bodyValue(input)
        .exchange(); // 요청 실행
  }

  private ResponseSpec sendPostRequestWithCookie(String path, String cookieName,
      String cookieValue) {
    return webTestClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(path)
            .build()
        )
        .header("User-Agent", "Mozilla/5.0 (compatible;)")
        .cookie(cookieName, cookieValue)
        .exchange(); // 요청 실행
  }

  private static <T> void errorDataValidate(EntityExchangeResult<Response<T>> response,
      ExceptionMessage expectedError) {
    Response<T> body = response.getResponseBody();
    assert body != null;
    assertThat(body).isNotNull();
    assertThat(body.getData()).isNull();
    assertThat(body.getErrors()).isNotNull();
    assertThat(body.getErrors().getErrorCode()).isEqualTo(expectedError.getErrorCode());
    assertThat(body.getErrors().getErrorDescription()).isEqualTo(expectedError.getDescription());
  }

  private static <T> void successDataValidate(EntityExchangeResult<Response<T>> response) {
    Response<T> body = response.getResponseBody();
    assert body != null;
    assertThat(body).isNotNull();
    assertThat(body.getErrors()).isNull();
    assertThat(body.getData()).isNotNull();
  }

  private String setupAndGetRefreshToken() {
    // Given
    String email = "example1@gmail.com";
    String password = "password_hash_1";
    AccountLoginInput input1 = new AccountLoginInput(email, password);

    // When & Then: Refresh Token을 반환
    return sendPostRequest("/api/account/login", input1)
        .expectStatus().isEqualTo(HttpStatus.OK)
        .expectBody(new ParameterizedTypeReference<Response<TokenOutput>>() {})
        .returnResult()
        .getResponseBody()
        .getData()
        .refreshToken(); // refreshToken 반환
  }

  private String getRefreshToken(Date date) {
    // Given
    String email = "example1@gmail.com";
    String password = "password_hash_1";

    // When & Then: Refresh Token을 반환
    return Jwts.builder()
//        .setHeaderParam("kid", "myKeyId")
        .setSubject(email)
        .claim("role", "")
        .setExpiration(date)
        .signWith(SignatureAlgorithm.HS256, securityProperties.getSecretKey())
        .compact();
  }

}
