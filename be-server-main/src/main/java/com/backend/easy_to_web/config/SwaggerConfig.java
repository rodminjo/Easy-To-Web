package com.backend.easy_to_web.config;

import com.backend.easy_to_web.common.infrastructure.controller.doc.ApiExceptionExplainParser;
import com.backend.easy_to_web.account.infrastructure.controller.doc.SecurityAPIDocCustomizer;
import com.backend.easy_to_web.config.properties.ProjectSocketProperties;
import com.backend.easy_to_web.config.properties.ServerProperties;
import com.backend.easy_to_web.security.authentication.manager.JwtManager;
import com.backend.easy_to_web.socket.controller.doc.WebSocketAPIDocCustomizer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

  private final ProjectSocketProperties projectSocketProperties;
  private final ServerProperties serverProperties;

  @Bean
  public OpenApiCustomizer openAPI(ApplicationContext applicationContext) {
    return openApi -> {
      SecurityAPIDocCustomizer.securityLoginCustomizer(applicationContext).customise(openApi);
      SecurityAPIDocCustomizer.securityLogoutCustomizer(applicationContext).customise(openApi);
      SecurityAPIDocCustomizer.securityTokenRefreshCustomizer(applicationContext).customise(openApi);
      SecurityAPIDocCustomizer.securityOAuth2AuthorizationCustomizer().customise(openApi);
      WebSocketAPIDocCustomizer.crdtLinkCustomizer(projectSocketProperties).customise(openApi);
      apiInfo().customise(openApi);
    };
  }

  @Bean
  public OperationCustomizer customErrorParse() {
    return (Operation operation, HandlerMethod handlerMethod) -> {
      ApiExceptionExplainParser.parse(operation, handlerMethod);
      return operation;
    };
  }


  private OpenApiCustomizer apiInfo() {
    Server server = new Server().url(serverProperties.getBeDomain());
    Info info = new Info()
        .title("Easy To Web API 가이드")
        .description("Easy To Web 유저 및 인증에 관한 REST API")
        .version("1.0.0")
        .contact(new Contact().email("rodminjo@naver.com"));

    return openApi -> {
      addSecurityInfo(openApi);
      openApi
          .info(info)
          .addServersItem(server);
    };
  }

  private void addSecurityInfo(OpenAPI openAPI) {
    openAPI.getComponents().addSecuritySchemes("accessToken", new SecurityScheme()
        .name(JwtManager.AUTHORIZATION)
        .type(SecurityScheme.Type.HTTP)
        .in(In.HEADER)
        .scheme(JwtManager.BEARER)
        .bearerFormat("JWT")
    );

    SecurityRequirement securityRequirement = new SecurityRequirement()
        .addList(JwtManager.ACCESS_TOKEN);
    openAPI.addSecurityItem(securityRequirement);
  }
}
