package com.backend.easy_to_web.config;

import com.backend.easy_to_web.config.properties.ProjectSocketProperties;
import com.backend.easy_to_web.socket.crdt.CRDTSyncSocketHandler;
import com.backend.easy_to_web.socket.crdt.CRDTSyncSocketDecoratorFactory;
import com.backend.easy_to_web.socket.crdt.CRDTSyncSocketInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final CRDTSyncSocketHandler crdtSyncSocketHandler;
  private final CRDTSyncSocketDecoratorFactory crdtSyncSocketDecoratorFactory;
  private final CRDTSyncSocketInterceptor crdtSyncSocketInterceptor;
  private final ProjectSocketProperties ProjectSocketProperties;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(crdtSyncSocketDecoratorFactory.decorate(crdtSyncSocketHandler), ProjectSocketProperties.getPath())
        .addInterceptors(crdtSyncSocketInterceptor)
        .setAllowedOrigins("*"); // CORS 허용
  }
}
