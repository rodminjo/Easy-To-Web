package com.backend.easy_to_web.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "jwt.token")
public class SecurityProperties {

  private String secretKey;
  private Long expireTime;
  private Long refreshExpireTime;

}
