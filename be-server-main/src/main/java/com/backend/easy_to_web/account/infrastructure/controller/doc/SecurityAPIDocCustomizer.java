package com.backend.easy_to_web.account.infrastructure.controller.doc;

import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiExceptionExplainParser;
import com.backend.easy_to_web.common.domain.exception.ExceptionMessage;
import com.backend.easy_to_web.security.authentication.filter.JwtLoginProcessingFilter;
import com.backend.easy_to_web.security.authentication.filter.JwtRefreshProcessingFilter;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.CookieParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityAPIDocCustomizer {

  // Security Login API 등록 로직
  public static OpenApiCustomizer securityLoginCustomizer(ApplicationContext applicationContext) {
    return openAPI -> processFilter(
        applicationContext,
        openAPI,
        JwtLoginProcessingFilter.class,
        "/api/account/login",
        "로그인",
        "로그인 API",
        createLoginRequestBody(),
        null,
        getSuccessResponse(),
        List.of(
            ExceptionMessage.LOGIN_INFO_NOT_FOUND,
            ExceptionMessage.LOGIN_INFO_INVALID
        )
    );
  }

  // Security OAuth2 Authorization API 등록 로직
  public static OpenApiCustomizer securityOAuth2AuthorizationCustomizer() {
    return openAPI -> {
      // path param: provider (google 고정)
      Parameter providerParam = new Parameter()
          .name("provider")
          .in("path")
          .required(true)
          .schema(new StringSchema()._enum(List.of("google")).example("google"))
          .description("OAuth2 provider (현재 google만 지원)");

      // query param: next (optional)
      Parameter nextParam = new Parameter()
          .name("next")
          .in("query")
          .required(false)
          .schema(new StringSchema().example("/list"))
          .description("로그인 성공 후 리다이렉트할 FE 상대 경로 (메인 : /list)");

      // 응답 스펙
      ApiResponses apiResponses = new ApiResponses();
      // 실패 시 302 + oauthError 쿼리
      addResponses(apiResponses, HttpStatus.FOUND, "302 (실패)", """
            Redirect back to FE login page with error query parameter.
            예: https://www.easytoweb.store?oauthError=LOGIN_CANCELLED
            """);

      // 잘못된 provider 등 400
      addResponses(apiResponses, HttpStatus.BAD_REQUEST, "400", """
            Invalid provider or configuration.
            """);

      // Operation
      Operation operation = new Operation()
          .summary("SNS 로그인 API (Google)")
          .description("Spring Security OAuth2 로그인 엔드포인트. provider는 현재 google만 지원합니다.")
          .addTagsItem("Account API")
          .addParametersItem(providerParam)
          .addParametersItem(nextParam)
          .responses(apiResponses);

      // Path 등록 (고정 경로 + 가변 경로)
      PathItem pathItem = new PathItem().get(operation);
      openAPI.getPaths().addPathItem("/oauth2/authorization/{provider}", pathItem);
    };
  }

  // Security Logout API 등록 로직
  public static OpenApiCustomizer securityLogoutCustomizer(ApplicationContext applicationContext) {
    return openAPI -> processFilter(
        applicationContext,
        openAPI,
        LogoutFilter.class,
        "/api/account/logout",
        "로그아웃",
        "로그아웃 API",
        null,
        null,
        getLogoutResponse(),
        List.of()
    );
  }


  // Security Token Refresh API 등록
  public static OpenApiCustomizer securityTokenRefreshCustomizer(
      ApplicationContext applicationContext) {
    return openAPI -> processFilter(
        applicationContext,
        openAPI,
        JwtRefreshProcessingFilter.class,
        "/api/account/reissue",
        "토큰 재발급",
        "토큰 재발급 API",
        null,
        createRefreshTokenCookieParameter(),
        getSuccessResponse(),
        List.of(
            ExceptionMessage.REFRESH_TOKEN_NOT_FOUND,
            ExceptionMessage.REFRESH_TOKEN_EXPIRED,
            ExceptionMessage.REFRESH_TOKEN_INVALID
        )
    );
  }

  // 실질적인 등록 프로세스
  // 필터에서 해당하는 필터를 찾아 필터 내용 등록
  private static void processFilter(
      ApplicationContext applicationContext,
      OpenAPI openAPI,
      Class<?> filterClass,
      String path,
      String summary,
      String description,
      RequestBody requestBody,
      Parameter parameter,
      ApiResponses apiResponses,
      List<ExceptionMessage> errorMappings
  ) {
    FilterChainProxy filterChainProxy = applicationContext.getBean(
        AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
        FilterChainProxy.class
    );

    for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
      Optional<?> optionalFilter = filterChain.getFilters().stream()
          .filter(filterClass::isInstance)
          .findAny();

      if (optionalFilter.isPresent()) {
        Operation operation = new Operation();
        operation.summary(summary).description(description);

        if (requestBody != null) {
          operation.requestBody(requestBody);
        }

        if (parameter != null) {
          operation.addParametersItem(parameter);
        }

        errorMappings.forEach(mapping -> addErrorResponses(apiResponses, mapping));

        operation.responses(apiResponses);
        operation.addTagsItem("Account API");

        PathItem pathItem = new PathItem().post(operation);
        openAPI.getPaths().addPathItem(path, pathItem);
      }
    }
  }

  private static RequestBody createLoginRequestBody() {
    Schema<?> schema = new ObjectSchema()
        .addProperty("email", new StringSchema())
        .addProperty("password", new StringSchema());
    return new RequestBody().content(
        new Content().addMediaType(
            org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            new MediaType().schema(schema)
        )
    );
  }

  private static Parameter createRefreshTokenCookieParameter() {
    // refreshToken 쿠키 파라미터 생성
    return new CookieParameter()
        .name("refreshToken")
        .description("리프레시 토큰 쿠키")
        .required(true) // 필수 여부 설정
        .schema(new StringSchema().example("cookieValue"));

  }

  private static ApiResponses getSuccessResponse() {
    ApiResponses apiResponses = new ApiResponses();
    String successSample = String.format("""
        {
          "data": {
            "tokenType": "%s",
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpvaG4iOiIxMjM0NTAwM",
            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpvaG4iOiIxMjM0NTA",
            "account": {
              "id": "UUID 형식",
              "nickname": "이름",
              "email": "example@example.com",
              "profileUrl: "/example/picture"
            }
          },
          "errors": null
        }
        """, JwtManager.BEARER);
    addResponses(apiResponses, HttpStatus.OK, String.valueOf(HttpStatus.OK.value()), successSample);
    return apiResponses;
  }

  private static ApiResponses getLogoutResponse() {
    ApiResponses apiResponses = new ApiResponses();
    String successSample = """
        {
          "data": "로그아웃 되었습니다"
        }
        """;
    addResponses(apiResponses, HttpStatus.OK, String.valueOf(HttpStatus.OK.value()), successSample);
    return apiResponses;
  }


  private static void addErrorResponses(ApiResponses apiResponses,
      ExceptionMessage exceptionMessage) {
    addResponses(
        apiResponses,
        exceptionMessage.getHttpStatus(),
        exceptionMessage.name(),
        ApiExceptionExplainParser.errorExampleString(exceptionMessage)
    );
  }

  private static void addResponses(ApiResponses apiResponses, HttpStatus httpStatus,
      String exampleName, String exampleValue) {
    ApiResponse response = apiResponses.computeIfAbsent(
        String.valueOf(httpStatus.value()),
        key -> new ApiResponse().description(httpStatus.getReasonPhrase())
    );

    Content content = response.getContent();
    if (content == null) {
      content = new Content();
      response.setContent(content);
    }

    org.springframework.http.MediaType mediaTypeKey = org.springframework.http.MediaType.APPLICATION_JSON;
    MediaType mediaType = content.get(mediaTypeKey.toString());
    if (mediaType == null) {
      mediaType = new MediaType();
      content.addMediaType(mediaTypeKey.toString(), mediaType);
    }

    Map<String, Example> examples = mediaType.getExamples();
    if (examples == null) {
      examples = new HashMap<>();
      mediaType.setExamples(examples);
    }

    examples.put(exampleName, new Example().value(exampleValue));
  }

}
