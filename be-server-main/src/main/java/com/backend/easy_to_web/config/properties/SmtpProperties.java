package com.backend.easy_to_web.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.mail.properties.mail.smtp")
public class SmtpProperties {

  private Boolean auth;
  private Integer connectionTimeout;
  private Integer timeout;
  private Integer writeTimeout;
  private Starttls starttls;


  @Setter
  @Getter
  @NoArgsConstructor
  public static class Starttls {

    private Boolean enable;
    private Boolean required;

  }
}
