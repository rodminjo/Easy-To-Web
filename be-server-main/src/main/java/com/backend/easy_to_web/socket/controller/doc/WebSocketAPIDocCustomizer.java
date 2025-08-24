package com.backend.easy_to_web.socket.controller.doc;

import com.backend.easy_to_web.config.properties.ProjectSocketProperties;
import com.backend.easy_to_web.socket.WebSocketExceptionMessage;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;

public class WebSocketAPIDocCustomizer {

  public static OpenApiCustomizer crdtLinkCustomizer(
      ProjectSocketProperties projectSocketProperties) {
    return openAPI -> {
      String path = projectSocketProperties.getPath();

      // 서버 도메인 가져오기 (없으면 기본값)
      String httpUrl = openAPI.getServers()
          .stream()
          .findFirst()
          .map(Server::getUrl)
          .orElse("your-domain");

      // ws 프로토콜로 변환
      String fullUrl = httpUrl.replaceFirst("^http", "ws") + path;

      // 그룹(태그)
      Tag tag = new Tag().name("WebSocket API")
          .description("CRDT 동기화 WebSocket 연결 API");

      // api
      Operation operation = new Operation()
          .summary("CRDT WebSocket 연결")
          .description(buildWebSocketDescription(fullUrl))
          .addTagsItem("WebSocket API");

      PathItem pathItem = new PathItem().get(operation);

      openAPI.addTagsItem(tag)
          .getPaths()
          .addPathItem(path, pathItem);
    };
  }

  private static String buildWebSocketDescription(String fullUrl) {
    StringBuilder errorDescription = new StringBuilder();

    errorDescription.append("""
        WebSocket을 이용한 CRDT 실시간 동기화 API입니다.

        ### 🔌 연결 방법
        - **URL:** `%s`
        - **프로토콜:** WebSocket (`Authorization_토큰` 형식의 서브프로토콜 사용)

        ### 💡 연결 예시 (JavaScript)
        ```javascript
        const provider = new WebsocketProvider("%s", roomName, doc, {
          params: {
            roomName: "d2b59c4e-af5c-4f69-98a2-9d5e28b33b71"
          },
          protocols: [
            "Authorization_eyJhbGciOiJIUzI1NiJ9..."
          ]
        });
        ```

        ### ❌ 정책 위반 (WebSocket 예외 목록 및 소켓 에러 코드)
        
        """.formatted(fullUrl, fullUrl));

    for (WebSocketExceptionMessage error : WebSocketExceptionMessage.values()) {
      errorDescription
          .append("- `")
          .append(error.getCloseStatus().getCode())
          .append("` **")
          .append(error.name())
          .append("**: ")
          .append(error.getExceptionMessage().getDescription())
          .append("\n");
    }

    return errorDescription.toString();
  }
}

