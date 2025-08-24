package com.backend.easy_to_web.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

  private String host;
  private Integer port;
  private String maxmemory;

}
