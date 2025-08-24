package com.backend.easy_to_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
public class WebSocketTuningConfig {
  @Bean
  public ServletServerContainerFactoryBean wsContainer() {
    var c = new ServletServerContainerFactoryBean();
    c.setMaxBinaryMessageBufferSize(64 * 1024);  // 기본 8192 → 상향
    c.setMaxTextMessageBufferSize(64 * 1024);    // 텍스트도 함께(예비)
    c.setAsyncSendTimeout(30_000L);              // 비동기 전송 허용 시간
    return c;
  }
}
