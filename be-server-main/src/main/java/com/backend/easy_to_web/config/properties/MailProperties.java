package com.backend.easy_to_web.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

  private String host;
  private Integer port;
  private String username;
  private String password;
  private Integer authCodeExpirationMin;

}
